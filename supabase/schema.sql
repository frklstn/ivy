-- =========================================================================
-- DATABASE SCHEMA FOR FINANCEAPP (IVY-INSPIRED FINANCE SAAS)
-- =========================================================================

-- Enable UUID extension
create extension if not exists "uuid-ossp";

-- 1. PROFILES TABLE (linked to Auth.Users)
create table public.profiles (
  id uuid references auth.users on delete cascade primary key,
  workspace_id uuid not null default gen_random_uuid(),
  email text,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on profiles
alter table public.profiles enable row level security;

create policy "Allow users to read their own profile"
  on public.profiles for select
  using ( auth.uid() = id );

create policy "Allow users to update their own profile"
  on public.profiles for update
  using ( auth.uid() = id );


-- 2. ACCOUNTS TABLE (Wallets)
create table public.accounts (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null,
  name text not null,
  currency text default 'IDR' not null,
  color integer not null default -16777216, -- Default color integer representation (e.g. black or default theme)
  icon text,
  order_num double precision not null default 0.0,
  include_in_balance boolean not null default true,
  is_deleted boolean not null default false,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on accounts
alter table public.accounts enable row level security;

create policy "Allow users to manage their own accounts"
  on public.accounts for all
  using ( auth.uid() = user_id );


-- 3. CATEGORIES TABLE
create table public.categories (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null,
  name text not null,
  color integer not null default -16777216,
  icon text,
  order_num double precision not null default 0.0,
  is_deleted boolean not null default false,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on categories
alter table public.categories enable row level security;

create policy "Allow users to manage their own categories"
  on public.categories for all
  using ( auth.uid() = user_id );


-- 4. TRANSACTIONS TABLE
create table public.transactions (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null,
  account_id uuid references public.accounts(id) on delete cascade not null,
  type text not null check (type in ('EXPENSE', 'INCOME', 'TRANSFER')),
  amount double precision not null,
  to_account_id uuid references public.accounts(id) on delete cascade,
  to_amount double precision,
  title text,
  description text,
  date_time timestamp with time zone default timezone('utc'::text, now()) not null,
  category_id uuid references public.categories(id) on delete set null,
  due_date timestamp with time zone,
  recurring_rule_id uuid,
  paid_for_date_time timestamp with time zone,
  attachment_url text,
  loan_id uuid,
  loan_record_id uuid,
  is_deleted boolean not null default false,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on transactions
alter table public.transactions enable row level security;

create policy "Allow users to manage their own transactions"
  on public.transactions for all
  using ( auth.uid() = user_id );


-- 5. BUDGETS TABLE
create table public.budgets (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null,
  name text not null,
  amount double precision not null,
  category_ids_serialized text, -- Komma terpisah (CSV) UUID kategori
  account_ids_serialized text, -- Komma terpisah (CSV) UUID wallet
  order_id double precision not null default 0.0,
  is_deleted boolean not null default false,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on budgets
alter table public.budgets enable row level security;

create policy "Allow users to manage their own budgets"
  on public.budgets for all
  using ( auth.uid() = user_id );


-- 6. LOANS TABLE
create table public.loans (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null,
  name text not null,
  amount double precision not null,
  type text not null, -- 'DEBT' (hutang kita ke orang) atau 'LOAN' (piutang orang ke kita)
  color integer not null default -16777216,
  icon text,
  order_num double precision not null default 0.0,
  account_id uuid references public.accounts(id) on delete set null,
  note text,
  date_time timestamp with time zone,
  is_deleted boolean not null default false,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on loans
alter table public.loans enable row level security;

create policy "Allow users to manage their own loans"
  on public.loans for all
  using ( auth.uid() = user_id );


-- 7. LOAN RECORDS TABLE
create table public.loan_records (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null,
  loan_id uuid references public.loans(id) on delete cascade not null,
  amount double precision not null,
  note text,
  date_time timestamp with time zone not null default timezone('utc'::text, now()),
  interest boolean not null default false,
  account_id uuid references public.accounts(id) on delete set null,
  converted_amount double precision,
  loan_record_type text not null, -- 'DECREASE' atau 'INCREASE'
  is_deleted boolean not null default false,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on loan_records
alter table public.loan_records enable row level security;

create policy "Allow users to manage their own loan records"
  on public.loan_records for all
  using ( auth.uid() = user_id );


-- 8. TAGS TABLE
create table public.tags (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null,
  name text not null,
  description text,
  color integer not null default -16777216,
  icon text,
  order_num double precision not null default 0.0,
  date_time timestamp with time zone not null default timezone('utc'::text, now()),
  is_deleted boolean not null default false,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on tags
alter table public.tags enable row level security;

create policy "Allow users to manage their own tags"
  on public.tags for all
  using ( auth.uid() = user_id );


-- 9. TAG ASSOCIATIONS TABLE
create table public.tag_associations (
  tag_id uuid references public.tags(id) on delete cascade not null,
  associated_id uuid not null, -- Bisa ID transaksi atau objek lain
  is_deleted boolean not null default false,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null,
  primary key (tag_id, associated_id)
);

-- Enable RLS on tag_associations
alter table public.tag_associations enable row level security;

create policy "Allow users to manage their own tag associations"
  on public.tag_associations for all
  using ( auth.uid() = user_id );


-- 10. PLANNED PAYMENT RULES TABLE
create table public.planned_payment_rules (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null,
  start_date timestamp with time zone,
  interval_n integer,
  interval_type text, -- 'DAY', 'WEEK', 'MONTH', 'YEAR'
  one_time boolean not null default false,
  type text not null check (type in ('EXPENSE', 'INCOME', 'TRANSFER')),
  account_id uuid references public.accounts(id) on delete cascade not null,
  amount double precision not null default 0.0,
  category_id uuid references public.categories(id) on delete set null,
  title text,
  description text,
  is_deleted boolean not null default false,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on planned_payment_rules
alter table public.planned_payment_rules enable row level security;

create policy "Allow users to manage their own planned payment rules"
  on public.planned_payment_rules for all
  using ( auth.uid() = user_id );


-- 11. APP SETTINGS TABLE
create table public.settings (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null,
  theme text default 'DARK' not null,
  currency text default 'IDR' not null,
  buffer_amount double precision default 0.0 not null,
  name text default 'My Wallet' not null,
  user_id uuid references auth.users on delete cascade not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable RLS on settings
alter table public.settings enable row level security;

create policy "Allow users to manage their own settings"
  on public.settings for all
  using ( auth.uid() = user_id );


-- =========================================================================
-- TRIGGERS & FUNCTIONS
-- =========================================================================

-- Trigger function to automatically create a profile and default workspace
create or replace function public.handle_new_user()
returns trigger as $$
declare
  new_workspace_id uuid;
begin
  new_workspace_id := gen_random_uuid();
  
  -- Create Profile
  insert into public.profiles (id, email, workspace_id)
  values (new.id, new.email, new_workspace_id);
  
  -- Create Default Settings for the user
  insert into public.settings (workspace_id, theme, currency, buffer_amount, name, user_id)
  values (new_workspace_id, 'DARK', 'IDR', 0.0, 'Main Wallet', new.id);

  -- Create Default Wallet / Account
  insert into public.accounts (workspace_id, name, currency, color, order_num, include_in_balance, user_id)
  values (new_workspace_id, 'Cash', 'IDR', -11513271, 0.0, true, new.id); -- Default color (e.g. green HSL representation)
  
  return new;
end;
$$ language plpgsql security definer;

-- Bind trigger to auth.users table
create or replace trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();
