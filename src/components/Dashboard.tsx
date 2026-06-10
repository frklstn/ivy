'use client';

import React, { useState, useEffect } from 'react';
import { supabase, Account, Category, Transaction, Profile } from '@/lib/supabase';
import { intToHex, hexToInt, formatCurrency, PREMIUM_COLORS } from '@/lib/utils';
import { 
  Wallet, Plus, Edit2, Trash2, LogOut, ArrowUpRight, ArrowDownLeft, 
  ArrowLeftRight, Calendar, Folder, Check, X, 
  TrendingUp, TrendingDown, Landmark, Sparkles, PieChart as ChartIcon, 
  Percent, User, AlertCircle
} from 'lucide-react';
import { 
  PieChart, Pie, Cell, ResponsiveContainer, Tooltip as ChartTooltip, 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Legend
} from 'recharts';

type Budget = {
  id: string;
  workspace_id: string;
  name: string;
  amount: number;
  category_ids_serialized: string | null;
  account_ids_serialized: string | null;
  order_id: number;
  is_deleted: boolean;
  user_id: string;
  created_at: string;
  updated_at: string;
};

type Loan = {
  id: string;
  workspace_id: string;
  name: string;
  amount: number;
  type: 'DEBT' | 'LOAN';
  color: number;
  icon: string | null;
  order_num: number;
  account_id: string | null;
  note: string | null;
  date_time: string | null;
  is_deleted: boolean;
  user_id: string;
  created_at: string;
  updated_at: string;
};

type LoanRecord = {
  id: string;
  workspace_id: string;
  loan_id: string;
  amount: number;
  note: string | null;
  date_time: string;
  interest: boolean;
  account_id: string | null;
  converted_amount: number | null;
  loan_record_type: 'DECREASE' | 'INCREASE';
  is_deleted: boolean;
  user_id: string;
  created_at: string;
  updated_at: string;
};

export default function Dashboard() {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  
  // Budgets & Loans states
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [loans, setLoans] = useState<Loan[]>([]);
  const [loanRecords, setLoanRecords] = useState<LoanRecord[]>([]);

  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'summary' | 'budgets' | 'loans' | 'reports'>('summary');
  const [isMounted, setIsMounted] = useState(false);

  // Modals Open/Close
  const [accountModalOpen, setAccountModalOpen] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState<Account | null>(null);
  const [accountName, setAccountName] = useState('');
  const [accountColor, setAccountColor] = useState(PREMIUM_COLORS[0].hex);
  const [accountCurrency, setAccountCurrency] = useState('IDR');
  const [accountIncludeInBalance, setAccountIncludeInBalance] = useState(true);

  const [categoryModalOpen, setCategoryModalOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [categoryName, setCategoryName] = useState('');
  const [categoryColor, setCategoryColor] = useState(PREMIUM_COLORS[0].hex);

  const [txModalOpen, setTxModalOpen] = useState(false);
  const [selectedTx, setSelectedTx] = useState<Transaction | null>(null);
  const [txType, setTxType] = useState<'EXPENSE' | 'INCOME' | 'TRANSFER'>('EXPENSE');
  const [txAmount, setTxAmount] = useState('');
  const [txAccountId, setTxAccountId] = useState('');
  const [txToAccountId, setTxToAccountId] = useState('');
  const [txCategoryId, setTxCategoryId] = useState('');
  const [txTitle, setTxTitle] = useState('');
  const [txDescription, setTxDescription] = useState('');
  const [txDateTime, setTxDateTime] = useState('');

  // Budget Modal
  const [budgetModalOpen, setBudgetModalOpen] = useState(false);
  const [selectedBudget, setSelectedBudget] = useState<Budget | null>(null);
  const [budgetName, setBudgetName] = useState('');
  const [budgetAmount, setBudgetAmount] = useState('');
  const [budgetCategoryIds, setBudgetCategoryIds] = useState<string[]>([]);
  const [budgetAccountIds, setBudgetAccountIds] = useState<string[]>([]);

  // Loan Modal
  const [loanModalOpen, setLoanModalOpen] = useState(false);
  const [selectedLoan, setSelectedLoan] = useState<Loan | null>(null);
  const [loanName, setLoanName] = useState('');
  const [loanAmount, setLoanAmount] = useState('');
  const [loanType, setLoanType] = useState<'DEBT' | 'LOAN'>('DEBT');
  const [loanColor, setLoanColor] = useState(PREMIUM_COLORS[0].hex);
  const [loanAccountId, setLoanAccountId] = useState('');
  const [loanNote, setLoanNote] = useState('');

  // Loan Record Modal (Payment)
  const [loanRecordModalOpen, setLoanRecordModalOpen] = useState(false);
  const [targetLoan, setTargetLoan] = useState<Loan | null>(null);
  const [loanRecordAmount, setLoanRecordAmount] = useState('');
  const [loanRecordType, setLoanRecordType] = useState<'DECREASE' | 'INCREASE'>('DECREASE');
  const [loanRecordAccountId, setLoanRecordAccountId] = useState('');
  const [loanRecordNote, setLoanRecordNote] = useState('');

  const fetchUserData = async () => {
    try {
      setLoading(true);
      const { data: { user } } = await supabase.auth.getUser();
      if (!user) return;

      // Fetch Profile
      const { data: prof, error: profError } = await supabase
        .from('profiles')
        .select('*')
        .eq('id', user.id)
        .single();

      if (profError) throw profError;
      setProfile(prof);

      // Fetch Accounts
      const { data: accs, error: accsError } = await supabase
        .from('accounts')
        .select('*')
        .eq('is_deleted', false)
        .order('order_num', { ascending: true });

      if (accsError) throw accsError;
      setAccounts(accs || []);

      // Fetch Categories
      const { data: cats, error: catsError } = await supabase
        .from('categories')
        .select('*')
        .eq('is_deleted', false)
        .order('order_num', { ascending: true });

      if (catsError) throw catsError;
      setCategories(cats || []);

      // Fetch Transactions
      const { data: txs, error: txsError } = await supabase
        .from('transactions')
        .select('*')
        .eq('is_deleted', false)
        .order('date_time', { ascending: false });

      if (txsError) throw txsError;
      setTransactions(txs || []);

      // Fetch Budgets
      const { data: bdgs, error: bdgsError } = await supabase
        .from('budgets')
        .select('*')
        .eq('is_deleted', false)
        .order('order_id', { ascending: true });

      if (bdgsError) throw bdgsError;
      setBudgets(bdgs || []);

      // Fetch Loans
      const { data: lns, error: lnsError } = await supabase
        .from('loans')
        .select('*')
        .eq('is_deleted', false)
        .order('order_num', { ascending: true });

      if (lnsError) throw lnsError;
      setLoans(lns || []);

      // Fetch Loan Records
      const { data: lrs, error: lrsError } = await supabase
        .from('loan_records')
        .select('*')
        .eq('is_deleted', false)
        .order('date_time', { ascending: false });

      if (lrsError) throw lrsError;
      setLoanRecords(lrs || []);

    } catch (err) {
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchUserData();
    }, 0);
    return () => clearTimeout(timer);
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsMounted(true);
    }, 0);
    return () => clearTimeout(timer);
  }, []);

  const handleLogout = async () => {
    await supabase.auth.signOut();
    window.location.reload();
  };

  // Calculations
  const accountBalances = (() => {
    const balances: Record<string, number> = {};
    accounts.forEach(acc => {
      balances[acc.id] = 0;
    });

    transactions.forEach(tx => {
      if (tx.is_deleted) return;
      const amt = tx.amount;
      
      if (tx.type === 'INCOME') {
        if (balances[tx.account_id] !== undefined) balances[tx.account_id] += amt;
      } else if (tx.type === 'EXPENSE') {
        if (balances[tx.account_id] !== undefined) balances[tx.account_id] -= amt;
      } else if (tx.type === 'TRANSFER') {
        if (balances[tx.account_id] !== undefined) balances[tx.account_id] -= amt;
        if (tx.to_account_id && balances[tx.to_account_id] !== undefined) {
          balances[tx.to_account_id] += tx.to_amount || amt;
        }
      }
    });

    // Adjust balances with loan record activities
    loanRecords.forEach(lr => {
      if (lr.is_deleted || !lr.account_id) return;
      const loan = loans.find(l => l.id === lr.loan_id);
      if (!loan) return;

      const factor = lr.loan_record_type === 'DECREASE' ? 1 : -1;
      
      if (balances[lr.account_id] !== undefined) {
        if (loan.type === 'DEBT') {
          // If we pay back our debt: Outflow from account
          balances[lr.account_id] -= lr.amount * factor;
        } else {
          // If they pay back their loan: Inflow to account
          balances[lr.account_id] += lr.amount * factor;
        }
      }
    });

    return balances;
  })();

  const totalBalance = accounts
    .filter(acc => acc.include_in_balance)
    .reduce((sum, acc) => sum + (accountBalances[acc.id] || 0), 0);

  const thisMonthStats = (() => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    
    let income = 0;
    let expense = 0;

    transactions.forEach(tx => {
      if (tx.is_deleted) return;
      const txDate = new Date(tx.date_time);
      if (txDate >= firstDay) {
        if (tx.type === 'INCOME') income += tx.amount;
        if (tx.type === 'EXPENSE') expense += tx.amount;
      }
    });

    return { income, expense };
  })();

  // Modal handler helpers
  const openAccountModal = (account?: Account) => {
    if (account) {
      setSelectedAccount(account);
      setAccountName(account.name);
      setAccountColor(intToHex(account.color));
      setAccountCurrency(account.currency);
      setAccountIncludeInBalance(account.include_in_balance);
    } else {
      setSelectedAccount(null);
      setAccountName('');
      setAccountColor(PREMIUM_COLORS[0].hex);
      setAccountCurrency('IDR');
      setAccountIncludeInBalance(true);
    }
    setAccountModalOpen(true);
  };

  const openCategoryModal = (category?: Category) => {
    if (category) {
      setSelectedCategory(category);
      setCategoryName(category.name);
      setCategoryColor(intToHex(category.color));
    } else {
      setSelectedCategory(null);
      setCategoryName('');
      setCategoryColor(PREMIUM_COLORS[0].hex);
    }
    setCategoryModalOpen(true);
  };

  const openTxModal = (tx?: Transaction) => {
    const localDateTimeStr = (date: Date) => {
      const offset = date.getTimezoneOffset();
      const localDate = new Date(date.getTime() - (offset * 60 * 1000));
      return localDate.toISOString().slice(0, 16);
    };

    if (tx) {
      setSelectedTx(tx);
      setTxType(tx.type);
      setTxAmount(tx.amount.toString());
      setTxAccountId(tx.account_id);
      setTxToAccountId(tx.to_account_id || '');
      setTxCategoryId(tx.category_id || '');
      setTxTitle(tx.title || '');
      setTxDescription(tx.description || '');
      setTxDateTime(localDateTimeStr(new Date(tx.date_time)));
    } else {
      setSelectedTx(null);
      setTxType('EXPENSE');
      setTxAmount('');
      setTxAccountId(accounts[0]?.id || '');
      setTxToAccountId('');
      setTxCategoryId(categories[0]?.id || '');
      setTxTitle('');
      setTxDescription('');
      setTxDateTime(localDateTimeStr(new Date()));
    }
    setTxModalOpen(true);
  };

  // 1. Wallets CRUD Actions
  const saveAccount = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!profile) return;

    try {
      const colorInt = hexToInt(accountColor);
      const accData = {
        name: accountName,
        currency: accountCurrency,
        color: colorInt,
        include_in_balance: accountIncludeInBalance,
        workspace_id: profile.workspace_id,
        user_id: profile.id,
      };

      if (selectedAccount) {
        const { error } = await supabase.from('accounts').update(accData).eq('id', selectedAccount.id);
        if (error) throw error;
      } else {
        const { error } = await supabase.from('accounts').insert({ ...accData, order_num: accounts.length * 1.0 });
        if (error) throw error;
      }
      setAccountModalOpen(false);
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  const deleteAccount = async (id: string) => {
    if (!confirm('Apakah Anda yakin ingin menghapus dompet ini?')) return;
    try {
      const { error } = await supabase.from('accounts').update({ is_deleted: true }).eq('id', id);
      if (error) throw error;
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  // 2. Categories CRUD Actions
  const saveCategory = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!profile) return;

    try {
      const colorInt = hexToInt(categoryColor);
      const catData = {
        name: categoryName,
        color: colorInt,
        workspace_id: profile.workspace_id,
        user_id: profile.id,
      };

      if (selectedCategory) {
        const { error } = await supabase.from('categories').update(catData).eq('id', selectedCategory.id);
        if (error) throw error;
      } else {
        const { error } = await supabase.from('categories').insert({ ...catData, order_num: categories.length * 1.0 });
        if (error) throw error;
      }
      setCategoryModalOpen(false);
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  const deleteCategory = async (id: string) => {
    if (!confirm('Apakah Anda yakin ingin menghapus kategori ini?')) return;
    try {
      const { error } = await supabase.from('categories').update({ is_deleted: true }).eq('id', id);
      if (error) throw error;
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  // 3. Transactions CRUD Actions
  const saveTx = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!profile) return;

    try {
      const amountVal = parseFloat(txAmount);
      if (isNaN(amountVal) || amountVal <= 0) return;

      const txData = {
        account_id: txAccountId,
        type: txType,
        amount: amountVal,
        to_account_id: txType === 'TRANSFER' ? txToAccountId : null,
        to_amount: txType === 'TRANSFER' ? amountVal : null,
        title: txTitle || null,
        description: txDescription || null,
        category_id: txType !== 'TRANSFER' && txCategoryId ? txCategoryId : null,
        date_time: new Date(txDateTime).toISOString(),
        workspace_id: profile.workspace_id,
        user_id: profile.id,
      };

      if (selectedTx) {
        const { error } = await supabase.from('transactions').update(txData).eq('id', selectedTx.id);
        if (error) throw error;
      } else {
        const { error } = await supabase.from('transactions').insert(txData);
        if (error) throw error;
      }
      setTxModalOpen(false);
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  const deleteTx = async (id: string) => {
    if (!confirm('Apakah Anda yakin ingin menghapus transaksi ini?')) return;
    try {
      const { error } = await supabase.from('transactions').update({ is_deleted: true }).eq('id', id);
      if (error) throw error;
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  // 4. Budgets CRUD Actions
  const openBudgetModal = (budget?: Budget) => {
    if (budget) {
      setSelectedBudget(budget);
      setBudgetName(budget.name);
      setBudgetAmount(budget.amount.toString());
      setBudgetCategoryIds(budget.category_ids_serialized ? budget.category_ids_serialized.split(',') : []);
      setBudgetAccountIds(budget.account_ids_serialized ? budget.account_ids_serialized.split(',') : []);
    } else {
      setSelectedBudget(null);
      setBudgetName('');
      setBudgetAmount('');
      setBudgetCategoryIds([]);
      setBudgetAccountIds([]);
    }
    setBudgetModalOpen(true);
  };

  const saveBudget = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!profile) return;

    try {
      const amountVal = parseFloat(budgetAmount);
      if (isNaN(amountVal) || amountVal <= 0) return;

      const budgetData = {
        name: budgetName,
        amount: amountVal,
        category_ids_serialized: budgetCategoryIds.length > 0 ? budgetCategoryIds.join(',') : null,
        account_ids_serialized: budgetAccountIds.length > 0 ? budgetAccountIds.join(',') : null,
        workspace_id: profile.workspace_id,
        user_id: profile.id,
      };

      if (selectedBudget) {
        const { error } = await supabase.from('budgets').update(budgetData).eq('id', selectedBudget.id);
        if (error) throw error;
      } else {
        const { error } = await supabase.from('budgets').insert({ ...budgetData, order_id: budgets.length * 1.0 });
        if (error) throw error;
      }
      setBudgetModalOpen(false);
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  const deleteBudget = async (id: string) => {
    if (!confirm('Apakah Anda yakin ingin menghapus anggaran ini?')) return;
    try {
      const { error } = await supabase.from('budgets').update({ is_deleted: true }).eq('id', id);
      if (error) throw error;
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  // 5. Loans & Records CRUD Actions
  const openLoanModal = (loan?: Loan) => {
    if (loan) {
      setSelectedLoan(loan);
      setLoanName(loan.name);
      setLoanAmount(loan.amount.toString());
      setLoanType(loan.type);
      setLoanColor(intToHex(loan.color));
      setLoanAccountId(loan.account_id || '');
      setLoanNote(loan.note || '');
    } else {
      setSelectedLoan(null);
      setLoanName('');
      setLoanAmount('');
      setLoanType('DEBT');
      setLoanColor(PREMIUM_COLORS[0].hex);
      setLoanAccountId(accounts[0]?.id || '');
      setLoanNote('');
    }
    setLoanModalOpen(true);
  };

  const saveLoan = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!profile) return;

    try {
      const amountVal = parseFloat(loanAmount);
      if (isNaN(amountVal) || amountVal <= 0) return;

      const loanData = {
        name: loanName,
        amount: amountVal,
        type: loanType,
        color: hexToInt(loanColor),
        account_id: loanAccountId || null,
        note: loanNote || null,
        workspace_id: profile.workspace_id,
        user_id: profile.id,
      };

      if (selectedLoan) {
        const { error } = await supabase.from('loans').update(loanData).eq('id', selectedLoan.id);
        if (error) throw error;
      } else {
        const { error } = await supabase.from('loans').insert({ ...loanData, order_num: loans.length * 1.0 });
        if (error) throw error;
      }
      setLoanModalOpen(false);
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  const deleteLoan = async (id: string) => {
    if (!confirm('Apakah Anda yakin ingin menghapus data hutang/piutang ini?')) return;
    try {
      const { error } = await supabase.from('loans').update({ is_deleted: true }).eq('id', id);
      if (error) throw error;
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  // Add Loan record (payment)
  const openLoanRecordModal = (loan: Loan) => {
    setTargetLoan(loan);
    setLoanRecordAmount('');
    setLoanRecordType('DECREASE'); // Decrease means paying off the loan/debt
    setLoanRecordAccountId(accounts[0]?.id || '');
    setLoanRecordNote('');
    setLoanRecordModalOpen(true);
  };

  const saveLoanRecord = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!profile || !targetLoan) return;

    try {
      const amountVal = parseFloat(loanRecordAmount);
      if (isNaN(amountVal) || amountVal <= 0) return;

      const recordData = {
        loan_id: targetLoan.id,
        amount: amountVal,
        loan_record_type: loanRecordType,
        account_id: loanRecordAccountId || null,
        note: loanRecordNote || null,
        date_time: new Date().toISOString(),
        workspace_id: profile.workspace_id,
        user_id: profile.id,
      };

      const { error } = await supabase.from('loan_records').insert(recordData);
      if (error) throw error;

      setLoanRecordModalOpen(false);
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  const deleteLoanRecord = async (id: string) => {
    if (!confirm('Hapus transaksi pembayaran ini?')) return;
    try {
      const { error } = await supabase.from('loan_records').update({ is_deleted: true }).eq('id', id);
      if (error) throw error;
      fetchUserData();
    } catch (err) {
      console.error(err);
    }
  };

  // Helper calculation for Budgets progress
  const getBudgetExpenses = (budget: Budget) => {
    const catIds = budget.category_ids_serialized ? budget.category_ids_serialized.split(',') : [];
    const accIds = budget.account_ids_serialized ? budget.account_ids_serialized.split(',') : [];
    const now = new Date();
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);

    let spent = 0;
    transactions.forEach(tx => {
      if (tx.is_deleted || tx.type !== 'EXPENSE') return;
      const txDate = new Date(tx.date_time);
      if (txDate < startOfMonth) return;

      // Filter by categories if specified
      if (catIds.length > 0 && (!tx.category_id || !catIds.includes(tx.category_id))) return;
      // Filter by accounts if specified
      if (accIds.length > 0 && !accIds.includes(tx.account_id)) return;

      spent += tx.amount;
    });

    return spent;
  };

  // Helper calculation for Loan status
  const getLoanRemainingAmount = (loan: Loan) => {
    let balance = loan.amount;
    const records = loanRecords.filter(r => r.loan_id === loan.id && !r.is_deleted);
    records.forEach(r => {
      if (r.loan_record_type === 'DECREASE') {
        balance -= r.amount;
      } else {
        balance += r.amount;
      }
    });
    return Math.max(0, balance);
  };

  // Laporan / Charts Processing
  const chartData = (() => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);

    // Group expenses by category
    const categoryTotals: Record<string, { name: string; value: number; color: string }> = {};

    transactions.forEach(tx => {
      if (tx.is_deleted || tx.type !== 'EXPENSE') return;
      const txDate = new Date(tx.date_time);
      if (txDate >= firstDay) {
        const cat = categories.find(c => c.id === tx.category_id);
        const catName = cat ? cat.name : 'Lainnya';
        const catColor = cat ? intToHex(cat.color) : '#64748b';
        const catId = tx.category_id || 'other';

        if (!categoryTotals[catId]) {
          categoryTotals[catId] = { name: catName, value: 0, color: catColor };
        }
        categoryTotals[catId].value += tx.amount;
      }
    });

    return Object.values(categoryTotals);
  })();

  const monthlyHistoryData = (() => {
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Agu', 'Sep', 'Okt', 'Nov', 'Des'];
    const now = new Date();
    const data = Array.from({ length: 6 }).map((_, idx) => {
      const d = new Date(now.getFullYear(), now.getMonth() - (5 - idx), 1);
      const mIdx = d.getMonth();
      const yr = d.getFullYear().toString().slice(-2);
      return {
        name: `${months[mIdx]} ${yr}`,
        Income: 0,
        Expense: 0,
        monthStart: d,
        monthEnd: new Date(d.getFullYear(), d.getMonth() + 1, 0, 23, 59, 59)
      };
    });

    transactions.forEach(tx => {
      if (tx.is_deleted) return;
      const txDate = new Date(tx.date_time);
      data.forEach(item => {
        if (txDate >= item.monthStart && txDate <= item.monthEnd) {
          if (tx.type === 'INCOME') item.Income += tx.amount;
          if (tx.type === 'EXPENSE') item.Expense += tx.amount;
        }
      });
    });

    return data;
  })();

  if (loading) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-bg-primary">
        <div className="h-10 w-10 animate-spin rounded-full border-4 border-brand-green border-t-transparent" />
        <p className="mt-4 text-sm text-text-secondary animate-pulse">Memuat data keuangan Anda...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-bg-primary pb-24">
      {/* Header */}
      <header className="border-b border-border-subtle bg-bg-secondary/50 backdrop-blur sticky top-0 z-30">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="h-10 w-10 rounded-xl bg-brand-green/20 flex items-center justify-center text-brand-green">
              <Landmark className="h-5 w-5" />
            </div>
            <div>
              <span className="text-xl font-bold tracking-tight text-white block">FinanceApp</span>
              <span className="text-xs text-text-tertiary">Ivy Wallet SaaS Edition</span>
            </div>
          </div>
          {/* Tabs Navigation (Desktop) */}
          <nav className="hidden md:flex items-center gap-1.5 bg-bg-secondary p-1 rounded-xl border border-border-subtle">
            {(['summary', 'budgets', 'loans', 'reports'] as const).map(tab => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`px-4 py-2 rounded-lg text-xs font-bold transition-all ${
                  activeTab === tab 
                    ? 'bg-brand-green text-bg-primary shadow'
                    : 'text-text-secondary hover:text-white'
                }`}
              >
                {tab === 'summary' ? 'RINGKASAN' : tab === 'budgets' ? 'ANGGARAN' : tab === 'loans' ? 'HUTANG/PIUTANG' : 'LAPORAN'}
              </button>
            ))}
          </nav>
          <div className="flex items-center gap-4">
            <span className="text-sm text-text-secondary hidden sm:inline-block">{profile?.email}</span>
            <button
              onClick={handleLogout}
              className="h-10 w-10 rounded-xl hover:bg-bg-tertiary flex items-center justify-center text-text-secondary hover:text-brand-red transition-all"
              title="Keluar"
            >
              <LogOut className="h-5 w-5" />
            </button>
          </div>
        </div>
      </header>

      {/* Main Content Container */}
      <main className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 mt-8">
        
        {/* Banner Section (Always Visible) */}
        <section className="glass-card p-8 rounded-2xl relative overflow-hidden mb-8">
          <div className="absolute top-0 right-0 w-64 h-full bg-gradient-to-l from-brand-green/10 to-transparent pointer-events-none" />
          <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div>
              <span className="text-xs font-semibold uppercase tracking-wider text-text-secondary flex items-center gap-2">
                <Sparkles className="h-3.5 w-3.5 text-brand-yellow" />
                Total Saldo Bersih
              </span>
              <h1 id="total-balance-heading" className="text-4xl sm:text-5xl font-black mt-2 tracking-tight text-white">
                {formatCurrency(totalBalance)}
              </h1>
            </div>
            <div className="flex items-center gap-6 border-t border-border-subtle pt-6 md:border-0 md:pt-0">
              <div className="flex items-center gap-3">
                <div className="h-12 w-12 rounded-xl bg-brand-green/15 flex items-center justify-center text-brand-green">
                  <TrendingUp className="h-6 w-6" />
                </div>
                <div>
                  <span className="text-xs text-text-secondary block">Pemasukan Bulan Ini</span>
                  <span className="text-lg font-bold text-white">{formatCurrency(thisMonthStats.income)}</span>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-12 w-12 rounded-xl bg-brand-red/15 flex items-center justify-center text-brand-red">
                  <TrendingDown className="h-6 w-6" />
                </div>
                <div>
                  <span className="text-xs text-text-secondary block">Pengeluaran Bulan Ini</span>
                  <span className="text-lg font-bold text-white">{formatCurrency(thisMonthStats.expense)}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* =====================================================================
            TAB 1: SUMMARY (RINGKASAN)
            ===================================================================== */}
        {activeTab === 'summary' && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Left Column: Wallets & Categories */}
            <div className="space-y-8 lg:col-span-1">
              {/* Wallets Card */}
              <section className="glass-card p-6 rounded-2xl">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-lg font-bold text-white flex items-center gap-2">
                    <Wallet className="h-5 w-5 text-brand-green" />
                    Dompet ({accounts.length})
                  </h2>
                  <button
                    onClick={() => openAccountModal()}
                    className="h-8 w-8 rounded-lg bg-brand-green/20 text-brand-green flex items-center justify-center hover:bg-brand-green hover:text-bg-primary transition-all"
                  >
                    <Plus className="h-4 w-4" />
                  </button>
                </div>
                <div className="space-y-3 max-h-[350px] overflow-y-auto pr-1">
                  {accounts.map(acc => {
                    const bal = accountBalances[acc.id] || 0;
                    const colorHex = intToHex(acc.color);
                    return (
                      <div key={acc.id} className="flex items-center justify-between p-3 rounded-xl bg-bg-secondary/40 border border-border-subtle group hover:border-border-active transition-all">
                        <div className="flex items-center gap-3">
                          <div className="h-4 w-4 rounded-full" style={{ backgroundColor: colorHex }} />
                          <div>
                            <span className="text-sm font-semibold text-white block">{acc.name}</span>
                            <span className="text-xs text-text-tertiary">{acc.currency}</span>
                          </div>
                        </div>
                        <div className="flex items-center gap-3">
                          <span className={`text-sm font-bold ${bal >= 0 ? 'text-brand-green' : 'text-brand-red'}`}>
                            {formatCurrency(bal, acc.currency)}
                          </span>
                          <div className="opacity-0 group-hover:opacity-100 flex items-center gap-1 transition-all">
                            <button onClick={() => openAccountModal(acc)} className="h-7 w-7 rounded-md bg-bg-tertiary text-text-secondary hover:text-white flex items-center justify-center"><Edit2 className="h-3.5 w-3.5" /></button>
                            <button onClick={() => deleteAccount(acc.id)} className="h-7 w-7 rounded-md bg-brand-red/10 text-brand-red hover:bg-brand-red hover:text-white flex items-center justify-center"><Trash2 className="h-3.5 w-3.5" /></button>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </section>

              {/* Categories Card */}
              <section className="glass-card p-6 rounded-2xl">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-lg font-bold text-white flex items-center gap-2">
                    <Folder className="h-5 w-5 text-brand-purple" />
                    Kategori ({categories.length})
                  </h2>
                  <button
                    onClick={() => openCategoryModal()}
                    className="h-8 w-8 rounded-lg bg-brand-purple/20 text-brand-purple flex items-center justify-center hover:bg-brand-purple hover:text-white transition-all"
                  >
                    <Plus className="h-4 w-4" />
                  </button>
                </div>
                <div className="space-y-2 max-h-[300px] overflow-y-auto pr-1">
                  {categories.map(cat => {
                    const colorHex = intToHex(cat.color);
                    return (
                      <div key={cat.id} className="flex items-center justify-between p-2.5 rounded-xl bg-bg-secondary/40 border border-border-subtle group hover:border-border-active transition-all">
                        <div className="flex items-center gap-2.5">
                          <div className="h-3 w-3 rounded-full" style={{ backgroundColor: colorHex }} />
                          <span className="text-sm text-white">{cat.name}</span>
                        </div>
                        <div className="opacity-0 group-hover:opacity-100 flex items-center gap-1 transition-all">
                          <button onClick={() => openCategoryModal(cat)} className="h-6 w-6 rounded-md bg-bg-tertiary text-text-secondary hover:text-white flex items-center justify-center"><Edit2 className="h-3 w-3" /></button>
                          <button onClick={() => deleteCategory(cat.id)} className="h-6 w-6 rounded-md bg-brand-red/10 text-brand-red hover:bg-brand-red hover:text-white flex items-center justify-center"><Trash2 className="h-3 w-3" /></button>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </section>
            </div>

            {/* Right Column: Recent Transactions */}
            <div className="lg:col-span-2">
              <section className="glass-card p-6 rounded-2xl flex flex-col h-full">
                <div className="flex items-center justify-between mb-6">
                  <div>
                    <h2 className="text-xl font-bold text-white">Histori Transaksi</h2>
                    <p className="text-xs text-text-secondary mt-1">Pengeluaran & pemasukan keuangan Anda</p>
                  </div>
                  <button onClick={() => openTxModal()} className="btn-primary">
                    <Plus className="h-4 w-4" />
                    Transaksi Baru
                  </button>
                </div>
                <div className="space-y-4 overflow-y-auto max-h-[500px]">
                  {transactions.map(tx => {
                    const acc = accounts.find(a => a.id === tx.account_id);
                    const toAcc = tx.to_account_id ? accounts.find(a => a.id === tx.to_account_id) : null;
                    const cat = tx.category_id ? categories.find(c => c.id === tx.category_id) : null;
                    const dateStr = new Date(tx.date_time).toLocaleDateString('id-ID', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' });

                    return (
                      <div key={tx.id} className="p-4 rounded-xl bg-bg-secondary/30 border border-border-subtle hover:border-border-active transition-all group flex items-center justify-between gap-4">
                        <div className="flex items-center gap-4">
                          <div className={`h-10 w-10 rounded-xl flex items-center justify-center ${
                            tx.type === 'INCOME' ? 'bg-brand-green/15 text-brand-green' :
                            tx.type === 'EXPENSE' ? 'bg-brand-red/15 text-brand-red' : 'bg-brand-blue/15 text-brand-blue'
                          }`}>
                            {tx.type === 'INCOME' ? <ArrowDownLeft className="h-5 w-5" /> :
                             tx.type === 'EXPENSE' ? <ArrowUpRight className="h-5 w-5" /> : <ArrowLeftRight className="h-5 w-5" />}
                          </div>
                          <div>
                            <span className="text-sm font-semibold text-white block">
                              {tx.title || (tx.type === 'TRANSFER' ? 'Transfer Saldo' : cat?.name || 'Lainnya')}
                            </span>
                            <span className="text-xs text-text-secondary block mt-0.5">
                              {acc?.name} {toAcc && `→ ${toAcc.name}`} | {dateStr}
                            </span>
                          </div>
                        </div>
                        <div className="flex items-center gap-4">
                          <span className={`text-base font-bold ${tx.type === 'INCOME' ? 'text-brand-green' : tx.type === 'EXPENSE' ? 'text-brand-red' : 'text-brand-blue'}`}>
                            {tx.type === 'INCOME' ? '+' : tx.type === 'EXPENSE' ? '-' : ''}
                            {formatCurrency(tx.amount, acc?.currency)}
                          </span>
                          <div className="opacity-0 group-hover:opacity-100 flex items-center gap-1">
                            <button onClick={() => openTxModal(tx)} className="h-7 w-7 rounded-md bg-bg-tertiary text-text-secondary hover:text-white flex items-center justify-center"><Edit2 className="h-3.5 w-3.5" /></button>
                            <button onClick={() => deleteTx(tx.id)} className="h-7 w-7 rounded-md bg-brand-red/10 text-brand-red hover:bg-brand-red hover:text-white flex items-center justify-center"><Trash2 className="h-3.5 w-3.5" /></button>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                  {transactions.length === 0 && <p className="text-xs text-text-tertiary text-center py-8">Belum ada transaksi.</p>}
                </div>
              </section>
            </div>
          </div>
        )}

        {/* =====================================================================
            TAB 2: BUDGETS (ANGGARAN)
            ===================================================================== */}
        {activeTab === 'budgets' && (
          <section className="glass-card p-6 rounded-2xl">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-xl font-bold text-white flex items-center gap-2">
                  <Percent className="h-6 w-6 text-brand-yellow" />
                  Batas Anggaran Bulanan
                </h2>
                <p className="text-xs text-text-secondary mt-1">Kontrol pengeluaran Anda dengan membuat batasan anggaran</p>
              </div>
              <button onClick={() => openBudgetModal()} className="btn-primary">
                <Plus className="h-4 w-4" />
                Anggaran Baru
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {budgets.map(b => {
                const spent = getBudgetExpenses(b);
                const percent = Math.min(100, b.amount > 0 ? (spent / b.amount) * 100 : 0);
                const isOverBudget = spent > b.amount;

                return (
                  <div key={b.id} className="p-5 rounded-2xl bg-bg-secondary/40 border border-border-subtle hover:border-border-active transition-all group flex flex-col justify-between">
                    <div>
                      <div className="flex items-start justify-between">
                        <div>
                          <h3 className="text-base font-bold text-white block">{b.name}</h3>
                          <span className="text-xs text-text-tertiary">Bulan Ini</span>
                        </div>
                        <div className="flex items-center gap-1.5 opacity-0 group-hover:opacity-100 transition-all">
                          <button onClick={() => openBudgetModal(b)} className="h-7 w-7 rounded-md bg-bg-tertiary text-text-secondary hover:text-white flex items-center justify-center"><Edit2 className="h-3.5 w-3.5" /></button>
                          <button onClick={() => deleteBudget(b.id)} className="h-7 w-7 rounded-md bg-brand-red/10 text-brand-red hover:bg-brand-red hover:text-white flex items-center justify-center"><Trash2 className="h-3.5 w-3.5" /></button>
                        </div>
                      </div>

                      {/* Spent info */}
                      <div className="flex justify-between items-baseline mt-4 mb-2">
                        <span className={`text-lg font-black ${isOverBudget ? 'text-brand-red' : 'text-white'}`}>
                          {formatCurrency(spent)} <span className="text-xs text-text-tertiary font-normal">terpakai dari</span>
                        </span>
                        <span className="text-sm font-semibold text-text-secondary">
                          {formatCurrency(b.amount)}
                        </span>
                      </div>

                      {/* Progress bar */}
                      <div className="w-full h-2.5 rounded-full bg-bg-tertiary overflow-hidden">
                        <div 
                          className={`h-full rounded-full transition-all duration-500 ${
                            isOverBudget ? 'bg-brand-red' : percent > 85 ? 'bg-brand-yellow' : 'bg-brand-green'
                          }`}
                          style={{ width: `${percent}%` }}
                        />
                      </div>
                    </div>
                    <div className="mt-4 flex items-center justify-between text-xs text-text-secondary">
                      <span>Persentase: {percent.toFixed(0)}%</span>
                      {isOverBudget && <span className="text-brand-red flex items-center gap-1 font-bold"><AlertCircle className="h-3.5 w-3.5" /> Melebihi Batas!</span>}
                    </div>
                  </div>
                );
              })}
              {budgets.length === 0 && (
                <div className="col-span-full py-16 text-center">
                  <p className="text-sm text-text-secondary">Belum ada batasan anggaran yang dikonfigurasi.</p>
                  <p className="text-xs text-text-tertiary mt-1">Tekan tombol Anggaran Baru untuk memulai pelacakan penghematan.</p>
                </div>
              )}
            </div>
          </section>
        )}

        {/* =====================================================================
            TAB 3: LOANS (HUTANG & PIUTANG)
            ===================================================================== */}
        {activeTab === 'loans' && (
          <section className="glass-card p-6 rounded-2xl">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-xl font-bold text-white flex items-center gap-2">
                  <Landmark className="h-6 w-6 text-brand-blue" />
                  Hutang & Piutang (Debts/Loans)
                </h2>
                <p className="text-xs text-text-secondary mt-1">Lacak pembayaran hutang Anda dan penagihan piutang dari orang lain</p>
              </div>
              <button onClick={() => openLoanModal()} className="btn-primary">
                <Plus className="h-4 w-4" />
                Data Baru
              </button>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {loans.map(loan => {
                const remaining = getLoanRemainingAmount(loan);
                const progressPercent = Math.min(100, ((loan.amount - remaining) / loan.amount) * 100);
                const isPaidOff = remaining <= 0;

                return (
                  <div key={loan.id} className="p-5 rounded-2xl bg-bg-secondary/40 border border-border-subtle hover:border-border-active transition-all flex flex-col justify-between relative overflow-hidden group">
                    {isPaidOff && (
                      <div className="absolute top-2 right-2 bg-brand-green/20 text-brand-green text-[10px] font-bold px-2 py-0.5 rounded-full border border-brand-green/30">
                        LUNAS
                      </div>
                    )}
                    <div>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                          <div 
                            className="h-10 w-10 rounded-xl flex items-center justify-center text-bg-primary"
                            style={{ backgroundColor: intToHex(loan.color) }}
                          >
                            <User className="h-5 w-5" />
                          </div>
                          <div>
                            <h3 className="text-base font-bold text-white">{loan.name}</h3>
                            <span className={`text-xs font-semibold ${loan.type === 'DEBT' ? 'text-brand-red' : 'text-brand-green'}`}>
                              {loan.type === 'DEBT' ? 'Hutang Saya (Harus Dibayar)' : 'Piutang Saya (Harus Ditagih)'}
                            </span>
                          </div>
                        </div>

                        <div className="opacity-0 group-hover:opacity-100 flex items-center gap-1 transition-all">
                          <button onClick={() => openLoanRecordModal(loan)} className="h-7 w-7 rounded-md bg-brand-green/20 text-brand-green hover:bg-brand-green hover:text-bg-primary flex items-center justify-center" title="Bayar / Cicil"><Plus className="h-3.5 w-3.5" /></button>
                          <button onClick={() => openLoanModal(loan)} className="h-7 w-7 rounded-md bg-bg-tertiary text-text-secondary hover:text-white flex items-center justify-center"><Edit2 className="h-3.5 w-3.5" /></button>
                          <button onClick={() => deleteLoan(loan.id)} className="h-7 w-7 rounded-md bg-brand-red/10 text-brand-red hover:bg-brand-red hover:text-white flex items-center justify-center"><Trash2 className="h-3.5 w-3.5" /></button>
                        </div>
                      </div>

                      <div className="flex justify-between items-baseline mt-6 mb-2">
                        <div>
                          <span className="text-xs text-text-tertiary block">Sisa Saldo</span>
                          <span className="text-2xl font-black text-white">{formatCurrency(remaining)}</span>
                        </div>
                        <div className="text-right">
                          <span className="text-xs text-text-tertiary block">Awal</span>
                          <span className="text-sm font-semibold text-text-secondary">{formatCurrency(loan.amount)}</span>
                        </div>
                      </div>

                      {/* Progress bar */}
                      <div className="w-full h-2 rounded-full bg-bg-tertiary overflow-hidden mt-2">
                        <div 
                          className="h-full rounded-full bg-brand-blue transition-all duration-500"
                          style={{ width: `${progressPercent}%` }}
                        />
                      </div>
                    </div>

                    {/* Records History for this loan */}
                    <div className="mt-4 border-t border-border-subtle pt-4">
                      <span className="text-xs font-semibold text-text-secondary block mb-2">Riwayat Pembayaran:</span>
                      <div className="space-y-1.5 max-h-[120px] overflow-y-auto pr-1">
                        {loanRecords.filter(r => r.loan_id === loan.id && !r.is_deleted).map(r => (
                          <div key={r.id} className="flex justify-between items-center text-xs p-1.5 rounded bg-bg-secondary/50 group/row hover:bg-bg-tertiary/30">
                            <span className="text-text-secondary flex items-center gap-1.5">
                              <Calendar className="h-3 w-3" />
                              {new Date(r.date_time).toLocaleDateString('id-ID', { day: 'numeric', month: 'short' })}
                              {r.note && <span className="text-text-tertiary">({r.note})</span>}
                            </span>
                            <div className="flex items-center gap-2">
                              <span className="font-semibold text-white">-{formatCurrency(r.amount)}</span>
                              <button 
                                onClick={() => deleteLoanRecord(r.id)}
                                className="text-brand-red opacity-0 group-hover/row:opacity-100 hover:scale-110 transition-all"
                              >
                                <X className="h-3.5 w-3.5" />
                              </button>
                            </div>
                          </div>
                        ))}
                        {loanRecords.filter(r => r.loan_id === loan.id && !r.is_deleted).length === 0 && (
                          <span className="text-xs text-text-tertiary italic">Belum ada cicilan/pembayaran.</span>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
              {loans.length === 0 && (
                <div className="col-span-full py-16 text-center">
                  <p className="text-sm text-text-secondary">Belum ada hutang atau piutang terdaftar.</p>
                  <p className="text-xs text-text-tertiary mt-1">Tekan tombol Data Baru untuk mendaftarkan catatan hutang/piutang.</p>
                </div>
              )}
            </div>
          </section>
        )}

        {/* =====================================================================
            TAB 4: REPORTS (LAPORAN)
            ===================================================================== */}
        {activeTab === 'reports' && isMounted && (
          <section className="space-y-8">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {/* Category Expenses Pie Chart */}
              <div className="glass-card p-6 rounded-2xl">
                <h3 className="text-lg font-bold text-white mb-4">Pengeluaran Berdasarkan Kategori (Bulan Ini)</h3>
                <div className="h-80 w-full">
                  {chartData.length > 0 ? (
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie
                          data={chartData}
                          cx="50%"
                          cy="50%"
                          labelLine={false}
                          label={({ name, percent }) => `${name}: ${percent !== undefined ? (percent * 100).toFixed(0) : '0'}%`}
                          outerRadius={80}
                          fill="#8884d8"
                          dataKey="value"
                        >
                          {chartData.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={entry.color} />
                          ))}
                        </Pie>
                        <ChartTooltip formatter={(val: number | string) => formatCurrency(Number(val ?? 0))} />
                      </PieChart>
                    </ResponsiveContainer>
                  ) : (
                    <div className="h-full flex items-center justify-center">
                      <p className="text-sm text-text-tertiary">Belum ada data pengeluaran bulan ini.</p>
                    </div>
                  )}
                </div>
              </div>

              {/* Monthly Inflow vs Outflow Trend */}
              <div className="glass-card p-6 rounded-2xl">
                <h3 className="text-lg font-bold text-white mb-4">Tren Bulanan (Inflow vs Outflow)</h3>
                <div className="h-80 w-full">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                      data={monthlyHistoryData}
                      margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
                    >
                      <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
                      <XAxis dataKey="name" stroke="#94a3b8" />
                      <YAxis stroke="#94a3b8" width={80} tickFormatter={(v) => `${(v / 1000).toFixed(0)}k`} />
                      <ChartTooltip formatter={(val: number | string) => formatCurrency(Number(val ?? 0))} />
                      <Legend />
                      <Bar dataKey="Income" name="Pemasukan" fill="#10b981" />
                      <Bar dataKey="Expense" name="Pengeluaran" fill="#ef4444" />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </div>
          </section>
        )}
      </main>

      {/* =====================================================================
          MOBILE TAB NAVIGATION BAR (Sticky Bottom)
          ===================================================================== */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-bg-secondary border-t border-border-subtle grid grid-cols-4 h-16 shadow-2xl">
        {(['summary', 'budgets', 'loans', 'reports'] as const).map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`flex flex-col items-center justify-center text-[10px] font-bold gap-1 transition-all ${
              activeTab === tab ? 'text-brand-green' : 'text-text-secondary hover:text-white'
            }`}
          >
            {tab === 'summary' && <Wallet className="h-5 w-5" />}
            {tab === 'budgets' && <Percent className="h-5 w-5" />}
            {tab === 'loans' && <Landmark className="h-5 w-5" />}
            {tab === 'reports' && <ChartIcon className="h-5 w-5" />}
            <span>
              {tab === 'summary' ? 'Ringkasan' : tab === 'budgets' ? 'Anggaran' : tab === 'loans' ? 'Hutang' : 'Laporan'}
            </span>
          </button>
        ))}
      </nav>

      {/* =====================================================================
          ALL MODAL DIALOGS
          ===================================================================== */}

      {/* 1. Account / Wallet Modal */}
      {accountModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fade-in">
          <div className="w-full max-w-md glass-card rounded-2xl overflow-hidden shadow-2xl border border-border-active">
            <div className="px-6 py-4 border-b border-border-subtle flex justify-between items-center bg-bg-secondary/40">
              <h3 className="text-lg font-bold text-white">{selectedAccount ? 'Edit Dompet' : 'Tambah Dompet Baru'}</h3>
              <button onClick={() => setAccountModalOpen(false)} className="text-text-secondary hover:text-white transition-all"><X className="h-5 w-5" /></button>
            </div>
            <form onSubmit={saveAccount} className="p-6 space-y-5">
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Nama Dompet</label>
                <input
                  type="text"
                  required
                  value={accountName}
                  onChange={(e) => setAccountName(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm transition-all focus:outline-none focus:border-brand-green"
                  placeholder="Uang Tunai, Rekening BCA, dll"
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Mata Uang</label>
                  <select
                    value={accountCurrency}
                    onChange={(e) => setAccountCurrency(e.target.value)}
                    className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-3 text-sm transition-all focus:outline-none focus:border-brand-green"
                  >
                    <option value="IDR">IDR (Rp)</option>
                    <option value="USD">USD ($)</option>
                    <option value="EUR">EUR (€)</option>
                  </select>
                </div>
                <div className="flex items-center h-full pt-6">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={accountIncludeInBalance}
                      onChange={(e) => setAccountIncludeInBalance(e.target.checked)}
                      className="rounded border-border-subtle bg-bg-secondary text-brand-green focus:ring-brand-green h-4 w-4"
                    />
                    <span className="text-xs text-text-secondary">Hitung di Saldo Total</span>
                  </label>
                </div>
              </div>
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Pilih Warna</label>
                <div className="flex flex-wrap gap-2.5">
                  {PREMIUM_COLORS.map(c => (
                    <button
                      key={c.hex}
                      type="button"
                      onClick={() => setAccountColor(c.hex)}
                      className={`h-8 w-8 rounded-full border-2 transition-all relative ${
                        accountColor === c.hex ? 'border-white scale-110 shadow-lg' : 'border-transparent'
                      }`}
                      style={{ backgroundColor: c.hex }}
                    >
                      {accountColor === c.hex && <Check className="h-4 w-4 text-bg-primary absolute inset-0 m-auto font-black" />}
                    </button>
                  ))}
                </div>
              </div>
              <div className="pt-4 border-t border-border-subtle flex justify-end gap-3">
                <button type="button" onClick={() => setAccountModalOpen(false)} className="btn-secondary">Batal</button>
                <button type="submit" className="btn-primary">Simpan</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 2. Category Modal */}
      {categoryModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="w-full max-w-md glass-card rounded-2xl overflow-hidden shadow-2xl border border-border-active">
            <div className="px-6 py-4 border-b border-border-subtle flex justify-between items-center bg-bg-secondary/40">
              <h3 className="text-lg font-bold text-white">{selectedCategory ? 'Edit Kategori' : 'Tambah Kategori Baru'}</h3>
              <button onClick={() => setCategoryModalOpen(false)} className="text-text-secondary hover:text-white transition-all"><X className="h-5 w-5" /></button>
            </div>
            <form onSubmit={saveCategory} className="p-6 space-y-5">
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Nama Kategori</label>
                <input
                  type="text"
                  required
                  value={categoryName}
                  onChange={(e) => setCategoryName(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm transition-all focus:outline-none focus:border-brand-green"
                  placeholder="Makanan, Transportasi, Hiburan, dll"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Pilih Warna</label>
                <div className="flex flex-wrap gap-2.5">
                  {PREMIUM_COLORS.map(c => (
                    <button
                      key={c.hex}
                      type="button"
                      onClick={() => setCategoryColor(c.hex)}
                      className={`h-8 w-8 rounded-full border-2 transition-all relative ${
                        categoryColor === c.hex ? 'border-white scale-110 shadow-lg' : 'border-transparent'
                      }`}
                      style={{ backgroundColor: c.hex }}
                    >
                      {categoryColor === c.hex && <Check className="h-4 w-4 text-bg-primary absolute inset-0 m-auto font-black" />}
                    </button>
                  ))}
                </div>
              </div>
              <div className="pt-4 border-t border-border-subtle flex justify-end gap-3">
                <button type="button" onClick={() => setCategoryModalOpen(false)} className="btn-secondary">Batal</button>
                <button type="submit" className="btn-primary">Simpan</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 3. Transaction Modal */}
      {txModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm overflow-y-auto">
          <div className="w-full max-w-lg glass-card rounded-2xl overflow-hidden shadow-2xl border border-border-active my-8">
            <div className="px-6 py-4 border-b border-border-subtle flex justify-between items-center bg-bg-secondary/40">
              <h3 className="text-lg font-bold text-white">{selectedTx ? 'Edit Transaksi' : 'Tambah Transaksi'}</h3>
              <button onClick={() => setTxModalOpen(false)} className="text-text-secondary hover:text-white transition-all"><X className="h-5 w-5" /></button>
            </div>
            <form onSubmit={saveTx} className="p-6 space-y-5">
              <div className="grid grid-cols-3 gap-2 bg-bg-secondary p-1 rounded-xl border border-border-subtle">
                {(['EXPENSE', 'INCOME', 'TRANSFER'] as const).map(type => (
                  <button
                    key={type}
                    type="button"
                    onClick={() => setTxType(type)}
                    className={`py-2 rounded-lg text-xs font-bold transition-all ${
                      txType === type 
                        ? type === 'EXPENSE' ? 'bg-brand-red text-white' 
                          : type === 'INCOME' ? 'bg-brand-green text-bg-primary' 
                          : 'bg-brand-blue text-white'
                        : 'text-text-secondary hover:text-white'
                    }`}
                  >
                    {type === 'EXPENSE' ? 'PENGELUARAN' : type === 'INCOME' ? 'PEMASUKAN' : 'TRANSFER'}
                  </button>
                ))}
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Jumlah Uang</label>
                  <input
                    type="number"
                    step="any"
                    required
                    value={txAmount}
                    onChange={(e) => setTxAmount(e.target.value)}
                    className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm font-bold focus:outline-none focus:border-brand-green"
                    placeholder="Rp 0"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">
                    {txType === 'TRANSFER' ? 'Dari Dompet' : 'Dompet'}
                  </label>
                  <select
                    value={txAccountId}
                    required
                    onChange={(e) => setTxAccountId(e.target.value)}
                    className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-3 text-sm focus:outline-none focus:border-brand-green"
                  >
                    <option value="" disabled>Pilih Dompet</option>
                    {accounts.map(a => (
                      <option key={a.id} value={a.id}>{a.name} ({formatCurrency(accountBalances[a.id] || 0, a.currency)})</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {txType === 'TRANSFER' ? (
                  <div>
                    <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Ke Dompet</label>
                    <select
                      value={txToAccountId}
                      required
                      onChange={(e) => setTxToAccountId(e.target.value)}
                      className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-3 text-sm focus:outline-none focus:border-brand-green"
                    >
                      <option value="" disabled>Pilih Tujuan</option>
                      {accounts.filter(a => a.id !== txAccountId).map(a => (
                        <option key={a.id} value={a.id}>{a.name}</option>
                      ))}
                    </select>
                  </div>
                ) : (
                  <div>
                    <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Kategori</label>
                    <select
                      value={txCategoryId}
                      onChange={(e) => setTxCategoryId(e.target.value)}
                      className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-3 text-sm focus:outline-none focus:border-brand-green"
                    >
                      <option value="">Tanpa Kategori</option>
                      {categories.map(c => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  </div>
                )}
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Tanggal & Waktu</label>
                  <input
                    type="datetime-local"
                    required
                    value={txDateTime}
                    onChange={(e) => setTxDateTime(e.target.value)}
                    className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-brand-green"
                  />
                </div>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Catatan / Keterangan Singkat</label>
                  <input
                    type="text"
                    value={txTitle}
                    onChange={(e) => setTxTitle(e.target.value)}
                    className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-brand-green"
                    placeholder="Beli Makan Siang, Gaji Kantor, dll (Opsional)"
                  />
                </div>
              </div>

              <div className="pt-4 border-t border-border-subtle flex justify-end gap-3">
                <button type="button" onClick={() => setTxModalOpen(false)} className="btn-secondary">Batal</button>
                <button type="submit" className="btn-primary">Simpan Transaksi</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 4. Budget Modal */}
      {budgetModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="w-full max-w-md glass-card rounded-2xl overflow-hidden shadow-2xl border border-border-active">
            <div className="px-6 py-4 border-b border-border-subtle flex justify-between items-center bg-bg-secondary/40">
              <h3 className="text-lg font-bold text-white">{selectedBudget ? 'Edit Anggaran' : 'Tambah Anggaran Baru'}</h3>
              <button onClick={() => setBudgetModalOpen(false)} className="text-text-secondary hover:text-white transition-all"><X className="h-5 w-5" /></button>
            </div>
            <form onSubmit={saveBudget} className="p-6 space-y-5">
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Nama Anggaran</label>
                <input
                  type="text"
                  required
                  value={budgetName}
                  onChange={(e) => setBudgetName(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-brand-green"
                  placeholder="Misalnya: Jajan Mingguan, Batas Belanja"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Batas Saldo Maksimal (Bulanan)</label>
                <input
                  type="number"
                  required
                  value={budgetAmount}
                  onChange={(e) => setBudgetAmount(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm font-bold focus:outline-none focus:border-brand-green"
                  placeholder="Rp 0"
                />
              </div>

              {/* Multi-select Checklist for categories */}
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Batasi pada Kategori Berikut (Opsional)</label>
                <div className="grid grid-cols-2 gap-2 max-h-[120px] overflow-y-auto pr-1">
                  {categories.map(cat => (
                    <label key={cat.id} className="flex items-center gap-2 text-xs text-text-secondary cursor-pointer">
                      <input
                        type="checkbox"
                        checked={budgetCategoryIds.includes(cat.id)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            setBudgetCategoryIds([...budgetCategoryIds, cat.id]);
                          } else {
                            setBudgetCategoryIds(budgetCategoryIds.filter(id => id !== cat.id));
                          }
                        }}
                        className="rounded border-border-subtle bg-bg-secondary text-brand-green focus:ring-brand-green h-3.5 w-3.5"
                      />
                      {cat.name}
                    </label>
                  ))}
                </div>
              </div>

              {/* Multi-select Checklist for wallets */}
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Batasi pada Dompet Berikut (Opsional)</label>
                <div className="grid grid-cols-2 gap-2 max-h-[120px] overflow-y-auto pr-1">
                  {accounts.map(acc => (
                    <label key={acc.id} className="flex items-center gap-2 text-xs text-text-secondary cursor-pointer">
                      <input
                        type="checkbox"
                        checked={budgetAccountIds.includes(acc.id)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            setBudgetAccountIds([...budgetAccountIds, acc.id]);
                          } else {
                            setBudgetAccountIds(budgetAccountIds.filter(id => id !== acc.id));
                          }
                        }}
                        className="rounded border-border-subtle bg-bg-secondary text-brand-green focus:ring-brand-green h-3.5 w-3.5"
                      />
                      {acc.name}
                    </label>
                  ))}
                </div>
              </div>

              <div className="pt-4 border-t border-border-subtle flex justify-end gap-3">
                <button type="button" onClick={() => setBudgetModalOpen(false)} className="btn-secondary">Batal</button>
                <button type="submit" className="btn-primary">Simpan</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 5. Loan Modal */}
      {loanModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="w-full max-w-md glass-card rounded-2xl overflow-hidden shadow-2xl border border-border-active">
            <div className="px-6 py-4 border-b border-border-subtle flex justify-between items-center bg-bg-secondary/40">
              <h3 className="text-lg font-bold text-white">{selectedLoan ? 'Edit Hutang/Piutang' : 'Tambah Hutang/Piutang'}</h3>
              <button onClick={() => setLoanModalOpen(false)} className="text-text-secondary hover:text-white transition-all"><X className="h-5 w-5" /></button>
            </div>
            <form onSubmit={saveLoan} className="p-6 space-y-5">
              <div className="grid grid-cols-2 gap-2 bg-bg-secondary p-1 rounded-xl border border-border-subtle">
                <button
                  type="button"
                  onClick={() => setLoanType('DEBT')}
                  className={`py-2 rounded-lg text-xs font-bold transition-all ${
                    loanType === 'DEBT' ? 'bg-brand-red text-white' : 'text-text-secondary hover:text-white'
                  }`}
                >
                  HUTANG SAYA
                </button>
                <button
                  type="button"
                  onClick={() => setLoanType('LOAN')}
                  className={`py-2 rounded-lg text-xs font-bold transition-all ${
                    loanType === 'LOAN' ? 'bg-brand-green text-bg-primary' : 'text-text-secondary hover:text-white'
                  }`}
                >
                  PIUTANG SAYA
                </button>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Nama Debitur/Kreditur</label>
                <input
                  type="text"
                  required
                  value={loanName}
                  onChange={(e) => setLoanName(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-brand-green"
                  placeholder="Nama Orang, Instansi, dll"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Jumlah Pokok</label>
                  <input
                    type="number"
                    required
                    value={loanAmount}
                    onChange={(e) => setLoanAmount(e.target.value)}
                    className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm font-bold focus:outline-none focus:border-brand-green"
                    placeholder="Rp 0"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Dompet Kas Awal</label>
                  <select
                    value={loanAccountId}
                    onChange={(e) => setLoanAccountId(e.target.value)}
                    className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-3 text-sm focus:outline-none focus:border-brand-green"
                  >
                    <option value="">Tanpa Dompet</option>
                    {accounts.map(a => (
                      <option key={a.id} value={a.id}>{a.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Warna Identifikasi</label>
                <div className="flex flex-wrap gap-2.5">
                  {PREMIUM_COLORS.map(c => (
                    <button
                      key={c.hex}
                      type="button"
                      onClick={() => setLoanColor(c.hex)}
                      className={`h-8 w-8 rounded-full border-2 transition-all relative ${
                        loanColor === c.hex ? 'border-white scale-110 shadow-lg' : 'border-transparent'
                      }`}
                      style={{ backgroundColor: c.hex }}
                    >
                      {loanColor === c.hex && <Check className="h-4 w-4 text-bg-primary absolute inset-0 m-auto font-black" />}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Catatan Detail</label>
                <textarea
                  value={loanNote}
                  onChange={(e) => setLoanNote(e.target.value)}
                  rows={2}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm resize-none focus:outline-none focus:border-brand-green"
                  placeholder="Detail pinjaman/kontak (Opsional)"
                />
              </div>

              <div className="pt-4 border-t border-border-subtle flex justify-end gap-3">
                <button type="button" onClick={() => setLoanModalOpen(false)} className="btn-secondary">Batal</button>
                <button type="submit" className="btn-primary">Simpan</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 6. Loan Record (Payment Transaction) Modal */}
      {loanRecordModalOpen && targetLoan && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="w-full max-w-md glass-card rounded-2xl overflow-hidden shadow-2xl border border-border-active">
            <div className="px-6 py-4 border-b border-border-subtle flex justify-between items-center bg-bg-secondary/40">
              <h3 className="text-lg font-bold text-white">Cicil / Lunasi: {targetLoan.name}</h3>
              <button onClick={() => setLoanRecordModalOpen(false)} className="text-text-secondary hover:text-white transition-all"><X className="h-5 w-5" /></button>
            </div>
            <form onSubmit={saveLoanRecord} className="p-6 space-y-5">
              <div className="grid grid-cols-2 gap-2 bg-bg-secondary p-1 rounded-xl border border-border-subtle">
                <button
                  type="button"
                  onClick={() => setLoanRecordType('DECREASE')}
                  className={`py-2 rounded-lg text-xs font-bold transition-all ${
                    loanRecordType === 'DECREASE' ? 'bg-brand-green text-bg-primary' : 'text-text-secondary hover:text-white'
                  }`}
                >
                  BAYAR / CICIL
                </button>
                <button
                  type="button"
                  onClick={() => setLoanRecordType('INCREASE')}
                  className={`py-2 rounded-lg text-xs font-bold transition-all ${
                    loanRecordType === 'INCREASE' ? 'bg-brand-red text-white' : 'text-text-secondary hover:text-white'
                  }`}
                >
                  TAMBAH UTANG
                </button>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Jumlah Pembayaran</label>
                <input
                  type="number"
                  required
                  value={loanRecordAmount}
                  onChange={(e) => setLoanRecordAmount(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm font-bold focus:outline-none focus:border-brand-green"
                  placeholder="Rp 0"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Gunakan Kas Dompet</label>
                <select
                  value={loanRecordAccountId}
                  required
                  onChange={(e) => setLoanRecordAccountId(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-3 text-sm focus:outline-none focus:border-brand-green"
                >
                  <option value="" disabled>Pilih Dompet</option>
                  {accounts.map(a => (
                    <option key={a.id} value={a.id}>{a.name} ({formatCurrency(accountBalances[a.id] || 0, a.currency)})</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">Catatan Pembayaran</label>
                <input
                  type="text"
                  value={loanRecordNote}
                  onChange={(e) => setLoanRecordNote(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-brand-green"
                  placeholder="Cicilan ke-1, Pembayaran Lunas, dll"
                />
              </div>

              <div className="pt-4 border-t border-border-subtle flex justify-end gap-3">
                <button type="button" onClick={() => setLoanRecordModalOpen(false)} className="btn-secondary">Batal</button>
                <button type="submit" className="btn-primary">Simpan Transaksi</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
