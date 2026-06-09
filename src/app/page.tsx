'use client';

import React, { useState, useEffect } from 'react';
import { supabase } from '@/lib/supabase';
import Auth from '@/components/Auth';
import Dashboard from '@/components/Dashboard';

export default function Home() {
  const [sessionActive, setSessionActive] = useState<boolean | null>(null);

  useEffect(() => {
    // Check active session on mount
    supabase.auth.getSession().then(({ data: { session } }) => {
      setSessionActive(!!session);
    });

    // Listen to auth state changes
    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      setSessionActive(!!session);
    });

    return () => {
      subscription.unsubscribe();
    };
  }, []);

  if (sessionActive === null) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-bg-primary">
        <div className="h-10 w-10 animate-spin rounded-full border-4 border-brand-green border-t-transparent" />
        <p className="mt-4 text-sm text-text-secondary animate-pulse">Memeriksa koneksi aman...</p>
      </div>
    );
  }

  return sessionActive ? (
    <Dashboard />
  ) : (
    <Auth onSessionActive={() => setSessionActive(true)} />
  );
}
