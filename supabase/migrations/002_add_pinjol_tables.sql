-- SUPABASE MIGRATIONS
-- File: 002_add_pinjol_tables.sql
-- Menambahkan tabel pendukung Pinjol Tracker dan menyelaraskan profiles untuk Ivy Wallet Android

-- Pastikan kolom workspace_id ada di tabel profiles untuk kompatibilitas multi-tenancy Ivy Wallet
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS workspace_id UUID;

-- 1. LOAN TRACKERS Table
CREATE TABLE IF NOT EXISTS public.loan_trackers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workspace_id UUID NOT NULL,
    app_name TEXT NOT NULL,
    category TEXT NOT NULL DEFAULT 'pinjol',
    amount_received NUMERIC(15, 2) NOT NULL,
    total_repayment NUMERIC(15, 2) NOT NULL,
    monthly_payment NUMERIC(15, 2) NOT NULL,
    tenure_months INTEGER NOT NULL CHECK (tenure_months > 0),
    due_day INTEGER NOT NULL CHECK (due_day BETWEEN 1 AND 31),
    start_date DATE NOT NULL,
    salary_date INTEGER CHECK (salary_date BETWEEN 1 AND 31),
    status TEXT CHECK (status IN ('active', 'paid_off')) NOT NULL DEFAULT 'active',
    notes TEXT,
    payment_frequency TEXT DEFAULT 'monthly',
    end_date DATE,
    total_remaining_balance NUMERIC(15, 2),
    penalty_fee NUMERIC(15, 2),
    can_early_payoff BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

ALTER TABLE public.loan_trackers ENABLE ROW LEVEL SECURITY;

-- RLS Policies for loan_trackers
DROP POLICY IF EXISTS "Members can view loan_trackers" ON public.loan_trackers;
DROP POLICY IF EXISTS "Members can manage loan_trackers" ON public.loan_trackers;
CREATE POLICY "Members can view loan_trackers" ON public.loan_trackers FOR SELECT USING (true);
CREATE POLICY "Members can manage loan_trackers" ON public.loan_trackers FOR ALL USING (true);

-- 2. DEBT PLANNER SETTINGS Table (Salary day settings)
CREATE TABLE IF NOT EXISTS public.debt_planner_settings (
    workspace_id UUID PRIMARY KEY,
    salary_day INTEGER NOT NULL DEFAULT 1 CHECK (salary_day BETWEEN 1 AND 31),
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

ALTER TABLE public.debt_planner_settings ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Members can view debt_planner_settings" ON public.debt_planner_settings;
DROP POLICY IF EXISTS "Members can manage debt_planner_settings" ON public.debt_planner_settings;
CREATE POLICY "Members can view debt_planner_settings" ON public.debt_planner_settings FOR SELECT USING (true);
CREATE POLICY "Members can manage debt_planner_settings" ON public.debt_planner_settings FOR ALL USING (true);

-- 3. INCOME TIMELINE Table (salary projection timelines)
CREATE TABLE IF NOT EXISTS public.income_timeline (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workspace_id UUID NOT NULL,
    effective_date DATE NOT NULL,
    monthly_income NUMERIC(15, 2) NOT NULL CHECK (monthly_income >= 0),
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

ALTER TABLE public.income_timeline ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Members can view income_timeline" ON public.income_timeline;
DROP POLICY IF EXISTS "Members can manage income_timeline" ON public.income_timeline;
CREATE POLICY "Members can view income_timeline" ON public.income_timeline FOR SELECT USING (true);
CREATE POLICY "Members can manage income_timeline" ON public.income_timeline FOR ALL USING (true);

-- 4. UPDATE handle_new_user() TRIGGER FUNCTION
-- Menginisialisasi record default untuk skema FinanceApp dan Ivy Wallet Android secara bersamaan
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
DECLARE
    new_workspace_id UUID;
BEGIN
    new_workspace_id := gen_random_uuid();

    -- A. Inisialisasi Profile User
    INSERT INTO public.profiles (id, email, full_name, avatar_url, workspace_id)
    VALUES (
        new.id,
        new.email,
        COALESCE(new.raw_user_meta_data->>'full_name', new.raw_user_meta_data->>'name'),
        new.raw_user_meta_data->>'avatar_url',
        new_workspace_id
    )
    ON CONFLICT (id) DO UPDATE
    SET email = new.email,
        full_name = COALESCE(new.raw_user_meta_data->>'full_name', new.raw_user_meta_data->>'name'),
        avatar_url = new.raw_user_meta_data->>'avatar_url',
        workspace_id = COALESCE(public.profiles.workspace_id, new_workspace_id);

    -- Ambil workspace_id yang terdaftar
    SELECT workspace_id INTO new_workspace_id FROM public.profiles WHERE id = new.id;

    -- B. Inisialisasi Tabel Workspaces (FinanceApp)
    INSERT INTO public.workspaces (id, name, owner_id)
    VALUES (new_workspace_id, 'Personal Workspace', new.id)
    ON CONFLICT (id) DO NOTHING;

    -- C. Inisialisasi Tabel Workspace Members (FinanceApp)
    INSERT INTO public.workspace_members (workspace_id, profile_id, role)
    VALUES (new_workspace_id, new.id, 'owner')
    ON CONFLICT (workspace_id, profile_id) DO NOTHING;

    -- D. Inisialisasi Tabel Wallets (FinanceApp)
    INSERT INTO public.wallets (workspace_id, name, type, balance, color, icon)
    VALUES 
        (new_workspace_id, 'Cash', 'cash', 0.00, '#10B981', 'banknote'),
        (new_workspace_id, 'Bank Account', 'bank', 0.00, '#3B82F6', 'landmark'),
        (new_workspace_id, 'E-Wallet', 'e-wallet', 0.00, '#8B5CF6', 'smartphone')
    ON CONFLICT DO NOTHING;

    -- E. Inisialisasi Tabel Debt Planner Settings (FinanceApp)
    INSERT INTO public.debt_planner_settings (workspace_id, salary_day)
    VALUES (new_workspace_id, 1)
    ON CONFLICT DO NOTHING;

    -- F. Inisialisasi Tabel Settings (Ivy Wallet Android)
    INSERT INTO public.settings (workspace_id, theme, currency, buffer_amount, name, user_id)
    VALUES (new_workspace_id, 'DARK', 'IDR', 0.0, 'Main Wallet', new.id)
    ON CONFLICT DO NOTHING;

    -- G. Inisialisasi Tabel Accounts / Wallets (Ivy Wallet Android)
    INSERT INTO public.accounts (workspace_id, name, currency, color, order_num, include_in_balance, user_id)
    VALUES (new_workspace_id, 'Cash', 'IDR', -11513271, 0.0, true, new.id)
    ON CONFLICT DO NOTHING;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
