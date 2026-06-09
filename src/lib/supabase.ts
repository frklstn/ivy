import { createClient } from '@supabase/supabase-js';

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL;
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY;

if (!supabaseUrl || !supabaseAnonKey) {
  throw new Error('Missing Supabase environment variables NEXT_PUBLIC_SUPABASE_URL or NEXT_PUBLIC_SUPABASE_ANON_KEY');
}

export const supabase = createClient(supabaseUrl, supabaseAnonKey, {
  auth: {
    persistSession: true,
    autoRefreshToken: true,
  },
});

export type Profile = {
  id: string;
  workspace_id: string;
  email: string | null;
  created_at: string;
  updated_at: string;
};

export type Account = {
  id: string;
  workspace_id: string;
  name: string;
  currency: string;
  color: number;
  icon: string | null;
  order_num: number;
  include_in_balance: boolean;
  is_deleted: boolean;
  user_id: string;
  created_at: string;
  updated_at: string;
};

export type Category = {
  id: string;
  workspace_id: string;
  name: string;
  color: number;
  icon: string | null;
  order_num: number;
  is_deleted: boolean;
  user_id: string;
  created_at: string;
  updated_at: string;
};

export type Transaction = {
  id: string;
  workspace_id: string;
  account_id: string;
  type: 'EXPENSE' | 'INCOME' | 'TRANSFER';
  amount: number;
  to_account_id: string | null;
  to_amount: number | null;
  title: string | null;
  description: string | null;
  date_time: string;
  category_id: string | null;
  due_date: string | null;
  recurring_rule_id: string | null;
  paid_for_date_time: string | null;
  attachment_url: string | null;
  loan_id: string | null;
  loan_record_id: string | null;
  is_deleted: boolean;
  user_id: string;
  created_at: string;
  updated_at: string;
};
