import sys
import requests
import os

TOKEN = os.environ.get('TELEGRAM_TOKEN')
CHAT_ID = os.environ.get('TELEGRAM_CHAT_ID')

def send_message(text):
    url = f"https://api.telegram.org/bot{TOKEN}/sendMessage"
    payload = {
        "chat_id": CHAT_ID,
        "text": text,
        "parse_mode": "HTML",
        "disable_web_page_preview": True
    }
    response = requests.post(url, json=payload)
    return response.json()

def edit_message(message_id, text):
    url = f"https://api.telegram.org/bot{TOKEN}/editMessageText"
    payload = {
        "chat_id": CHAT_ID,
        "message_id": message_id,
        "text": text,
        "parse_mode": "HTML",
        "disable_web_page_preview": True
    }
    response = requests.post(url, json=payload)
    return response.json()

def delete_message(message_id):
    url = f"https://api.telegram.org/bot{TOKEN}/deleteMessage"
    payload = {
        "chat_id": CHAT_ID,
        "message_id": message_id
    }
    response = requests.post(url, json=payload)
    return response.json()

def send_document(caption, file_path):
    url = f"https://api.telegram.org/bot{TOKEN}/sendDocument"
    with open(file_path, 'rb') as f:
        files = {'document': f}
        payload = {
            "chat_id": CHAT_ID,
            "caption": caption,
            "parse_mode": "HTML"
        }
        response = requests.post(url, data=payload, files=files)
    return response.json()

if __name__ == "__main__":
    action = sys.argv[1]
    
    if action == "start":
        commit_msg = sys.argv[2]
        repo = sys.argv[3]
        sha = sys.argv[4][:7]
        text = (f"<b>🚀 Memulai Build Ivy Wallet</b>\n\n"
                f"📝 <b>Commit:</b> {commit_msg}\n"
                f"branch: <code>main</code> ({sha})\n"
                f"🔄 <b>Status:</b> Sedang mempersiapkan...")
        res = send_message(text)
        if res.get('ok'):
            print(res['result']['message_id'])

    elif action == "update":
        msg_id = sys.argv[2]
        status = sys.argv[3]
        commit_msg = sys.argv[4]
        sha = sys.argv[5][:7]
        log_preview = sys.argv[6] if len(sys.argv) > 6 else ""
        
        text = (f"<b>🚀 Membangun Ivy Wallet</b>\n\n"
                f"📝 <b>Commit:</b> {commit_msg}\n"
                f"branch: <code>main</code> ({sha})\n\n"
                f"🔄 <b>Status:</b> {status}\n")
        
        if log_preview:
            text += f"\n📋 <b>Live Log:</b>\n<code>{log_preview}</code>"
            
        edit_message(msg_id, text)

    elif action == "success":
        msg_id = sys.argv[2]
        file_path = sys.argv[3]
        commit_msg = sys.argv[4]
        sha = sys.argv[5][:7]
        
        # Edit status terakhir
        text = (f"<b>✅ Build Berhasil!</b>\n\n"
                f"📝 <b>Commit:</b> {commit_msg}\n"
                f"branch: <code>main</code> ({sha})\n"
                f"📦 File APK sedang dikirim...")
        edit_message(msg_id, text)
        
        # Kirim File
        caption = (f"<b>📦 Ivy Wallet - Demo APK</b>\n\n"
                   f"✨ <b>Changelog:</b>\n{commit_msg}\n\n"
                   f"🆔 Build: <code>{sha}</code>")
        send_document(caption, file_path)

    elif action == "fail":
        msg_id = sys.argv[2]
        commit_msg = sys.argv[3]
        sha = sys.argv[4][:7]
        log_file_path = sys.argv[5] if len(sys.argv) > 5 else None
        
        # Coba ekstraksi bagian "What went wrong" dari log
        detailed_error = "Tidak dapat mengekstraksi detail error."
        if log_file_path and os.path.exists(log_file_path):
            with open(log_file_path, 'r') as f:
                content = f.read()
                if "* What went wrong:" in content:
                    detailed_error = content.split("* What went wrong:")[1].split("* Try:")[0].strip()
        
        text = (f"<b>❌ Build Gagal!</b>\n\n"
                f"📝 <b>Commit:</b> {commit_msg}\n"
                f"branch: <code>main</code> ({sha})\n\n"
                f"🔍 <b>Penyebab (Ringkasan):</b>\n<code>{detailed_error}</code>\n\n"
                f"⚠️ Detail lengkap ada pada file log terlampir.")
        edit_message(msg_id, text)
        
        if log_file_path and os.path.exists(log_file_path):
            caption = f"<b>📄 Full Build Log</b>\n🆔 Build: <code>{sha}</code>"
            send_document(caption, log_file_path)

    elif action == "delete":
        msg_id = sys.argv[2]
        delete_message(msg_id)

    elif action == "cancel":
        msg_id = sys.argv[2]
        commit_msg = sys.argv[3]
        sha = sys.argv[4][:7]
        text = (f"<b>⚪ Build Dibatalkan</b>\n\n"
                f"📝 <b>Commit:</b> {commit_msg}\n"
                f"Status: Dihentikan karena ada build baru atau dibatalkan manual.")
        edit_message(msg_id, text)
