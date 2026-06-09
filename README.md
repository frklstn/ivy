# FinanceApp (Ivy Wallet SaaS Clone)

Full-stack finance management app inspired by Ivy Wallet.

## Language Policy
This project uses Indonesian as the default language for:
- Documentation
- AI-generated explanations
- Code comments

Only code identifiers remain in English.

## Tech Stack
- Next.js (App Router)
- Supabase (Auth + Database)
- TailwindCSS
- Vercel (Deployment)
- Flutter WebView (Android wrapper APK)

---

## Features
- Authentication (Supabase)
- Wallet management
- Transaction tracking
- Budget system
- Savings goals
- Debt & loan tracker
- Financial reports
- Admin settings

---

## Architecture
Frontend → Next.js (Vercel)
Backend → Supabase (PostgreSQL + Auth)
Mobile → Flutter WebView APK

---

## Setup

### 1. Install dependencies
npm install

### 2. Setup environment
Create `.env.local`

NEXT_PUBLIC_SUPABASE_URL=your_url
NEXT_PUBLIC_SUPABASE_ANON_KEY=your_key

---

### 3. Run development
npm run dev

---

## Database
Use Supabase SQL schema from /supabase/schema.sql

Enable RLS on all tables.

---

## Deployment

### Web (Vercel)
Push to GitHub → Import to Vercel → Done

### Android APK
Build Flutter WebView wrapper:
cd mobile
flutter build apk --release

APK will load deployed web app.

---

## Notes
- This is FULL ONLINE SaaS system
- No offline database (Supabase only)