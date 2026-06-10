'use client';

import React, { useState } from 'react';
import { supabase } from '@/lib/supabase';
import { Mail, Lock, LogIn, ArrowRight } from 'lucide-react';

type AuthProps = {
  onSessionActive: () => void;
};

export default function Auth({ onSessionActive }: AuthProps) {
  const [isSignUp, setIsSignUp] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const handleAuth = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setErrorMsg(null);
    setSuccessMsg(null);

    try {
      if (isSignUp) {
        const { data, error } = await supabase.auth.signUp({
          email,
          password,
        });

        if (error) throw error;
        
        if (data.user && data.session) {
          onSessionActive();
        } else {
          setSuccessMsg('Registrasi berhasil! Silakan periksa email Anda untuk memverifikasi akun.');
        }
      } else {
        const { error } = await supabase.auth.signInWithPassword({
          email,
          password,
        });

        if (error) throw error;
        onSessionActive();
      }
    } catch (err) {
      const error = err as Error;
      setErrorMsg(error.message || 'Terjadi kesalahan saat memproses autentikasi.');
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = async () => {
    setErrorMsg(null);
    setSuccessMsg(null);
    try {
      const { error } = await supabase.auth.signInWithOAuth({
        provider: 'google',
        options: {
          redirectTo: typeof window !== 'undefined' ? window.location.origin : '',
        },
      });
      if (error) throw error;
    } catch (err) {
      const error = err as Error;
      setErrorMsg(error.message || 'Gagal masuk menggunakan Google.');
    }
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-bg-primary px-4 py-12 sm:px-6 lg:px-8 relative overflow-hidden">
      {/* Decorative blurred background shapes */}
      <div className="absolute top-1/4 left-1/4 -translate-x-1/2 -translate-y-1/2 w-72 h-72 rounded-full bg-brand-green/10 blur-3xl pointer-events-none" />
      <div className="absolute bottom-1/4 right-1/4 translate-x-1/2 translate-y-1/2 w-80 h-80 rounded-full bg-brand-purple/10 blur-3xl pointer-events-none" />

      <div className="w-full max-w-md space-y-8 glass-card p-8 rounded-2xl relative z-10">
        <div className="text-center">
          <div className="inline-flex h-16 w-16 items-center justify-center rounded-2xl bg-brand-green/20 text-brand-green mb-4">
            <LogIn className="h-8 w-8" />
          </div>
          <h1 id="auth-title" className="text-3xl font-extrabold tracking-tight text-white font-sans">
            FinanceApp
          </h1>
          <p className="mt-2 text-sm text-text-secondary">
            {isSignUp ? 'Buat akun untuk mengelola keuangan Anda' : 'Masuk untuk mengelola keuangan Anda'}
          </p>
        </div>

        <form className="mt-8 space-y-5" onSubmit={handleAuth} id="auth-form">
          <div className="space-y-4 rounded-md">
            <div>
              <label htmlFor="email-address" className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">
                Alamat Email
              </label>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                  <Mail className="h-5 w-5 text-text-tertiary" />
                </div>
                <input
                  id="email-address"
                  name="email"
                  type="email"
                  autoComplete="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 pl-10 pr-4 text-sm transition-all focus:outline-none focus:border-brand-green focus:ring-2 focus:ring-brand-green/20"
                  placeholder="name@example.com"
                />
              </div>
            </div>

            <div>
              <label htmlFor="password" className="block text-xs font-semibold uppercase tracking-wider text-text-secondary mb-2">
                Kata Sandi
              </label>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                  <Lock className="h-5 w-5 text-text-tertiary" />
                </div>
                <input
                  id="password"
                  name="password"
                  type="password"
                  autoComplete="current-password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full bg-bg-secondary border border-border-subtle text-text-primary rounded-xl py-3 pl-10 pr-4 text-sm transition-all focus:outline-none focus:border-brand-green focus:ring-2 focus:ring-brand-green/20"
                  placeholder="••••••••"
                />
              </div>
            </div>
          </div>

          {errorMsg && (
            <div className="rounded-lg bg-brand-red/10 p-3 text-sm text-brand-red border border-brand-red/20" id="auth-error">
              {errorMsg}
            </div>
          )}

          {successMsg && (
            <div className="rounded-lg bg-brand-green/10 p-3 text-sm text-brand-green border border-brand-green/20" id="auth-success">
              {successMsg}
            </div>
          )}

          <div>
            <button
              id="auth-submit-btn"
              type="submit"
              disabled={loading}
              className="btn-primary w-full group relative overflow-hidden"
            >
              {loading ? (
                <span className="flex items-center gap-2">
                  <span className="h-4 w-4 animate-spin rounded-full border-2 border-bg-primary border-t-transparent" />
                  Memproses...
                </span>
              ) : (
                <span className="flex items-center justify-center gap-2 w-full">
                  {isSignUp ? 'Daftar Sekarang' : 'Masuk'}
                  <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
                </span>
              )}
            </button>
          </div>
        </form>

        {/* Separator OR */}
        <div className="relative flex py-2 items-center">
          <div className="flex-grow border-t border-border-subtle"></div>
          <span className="flex-shrink mx-4 text-text-tertiary text-xs uppercase tracking-wider font-semibold">atau</span>
          <div className="flex-grow border-t border-border-subtle"></div>
        </div>

        {/* Google OAuth Login Button */}
        <div>
          <button
            id="google-login-btn"
            type="button"
            onClick={handleGoogleLogin}
            className="w-full flex items-center justify-center gap-3 bg-white text-gray-900 font-semibold rounded-xl py-3 px-4 text-sm transition-all hover:bg-gray-100 active:scale-98 border border-gray-200"
          >
            <svg className="h-5 w-5 shrink-0" viewBox="0 0 24 24" width="24" height="24" xmlns="http://www.w3.org/2000/svg">
              <g transform="matrix(1, 0, 0, 1, 0, 0)">
                <path d="M21.35,11.1H12v2.7h5.38c-0.24,1.28 -0.96,2.37 -2.04,3.1v2.58h3.3c1.93,-1.78 3.04,-4.4 3.04,-7.39c0,-0.7 -0.06,-1.37 -0.18,-1.99Z" fill="#4285F4" />
                <path d="M12,20.62c2.43,0 4.47,-0.8 5.96,-2.18l-3.3,-2.58c-0.92,0.62 -2.1,0.98 -3.48,0.98c-2.67,0 -4.93,-1.81 -5.74,-4.24H2.03v2.66c1.51,2.99 4.6,5.36 9.97,5.36Z" fill="#34A853" />
                <path d="M6.26,12.6c-0.21,-0.62 -0.33,-1.28 -0.33,-1.96s0.12,-1.34 0.33,-1.96V6.02H2.03c-0.73,1.46 -1.15,3.1 -1.15,4.82s0.42,3.36 1.15,4.82l4.23,-3.06Z" fill="#FBBC05" />
                <path d="M12,5.38c1.32,0 2.51,0.45 3.44,1.35l2.58,-2.58C16.46,2.69 14.43,1.88 12,1.88c-5.37,0 -8.46,2.37 -9.97,5.36l4.23,3.06c0.81,-2.43 3.07,-4.24 5.74,-4.24Z" fill="#EA4335" />
              </g>
            </svg>
            Masuk dengan Google
          </button>
        </div>

        <div className="text-center mt-6">
          <button
            id="auth-toggle-btn"
            type="button"
            onClick={() => {
              setIsSignUp(!isSignUp);
              setErrorMsg(null);
              setSuccessMsg(null);
            }}
            className="text-sm text-brand-green hover:underline focus:outline-none transition-all"
          >
            {isSignUp ? 'Sudah punya akun? Masuk di sini' : 'Belum punya akun? Daftar gratis'}
          </button>
        </div>
      </div>
    </div>
  );
}

