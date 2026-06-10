#!/bin/bash

MSG_ID=$1
COMMIT_MSG=$2
SHA=$3
LOG_FILE="build.log"

echo "Memulai monitoring build.log..."

# Loop selama build.log masih ada atau build masih berjalan
while [ -f "build_running.lock" ]; do
    if [ -f "$LOG_FILE" ]; then
        # Ambil 5 baris terakhir, bersihkan karakter aneh/escape codes
        LOG_PREVIEW=$(tail -n 5 "$LOG_FILE" | sed 's/\x1b\[[0-9;]*m//g' | sed 's/<[^>]*>//g' | cut -c 1-200)
        
        # Kirim update ke Telegram
        python3 scripts/telegram_notifier.py update "$MSG_ID" "Membangun APK (assembleDemo)..." "$COMMIT_MSG" "$SHA" "$LOG_PREVIEW"
    fi
    sleep 10 # Update setiap 10 detik agar tidak kena rate limit Telegram
done

echo "Monitoring selesai."
