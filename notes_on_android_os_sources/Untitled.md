The instructions to fetch **AOSP Android 13.0.0_r35** source code:

---

### ✅ Prerequisites (Ubuntu 20.04+ recommended):

1. Install required dependencies:
```bash
sudo apt update
sudo apt install git-core gnupg flex bison build-essential zip curl zlib1g-dev gcc-multilib g++-multilib \
    libc6-dev-i386 lib32ncurses5-dev x11proto-core-dev libx11-dev lib32z-dev ccache libgl1-mesa-dev \
    libxml2-utils xsltproc unzip fontconfig
```

2. Install the `repo` tool (if you haven’t already):
```bash
mkdir -p ~/bin
curl https://storage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
export PATH=~/bin:$PATH
```

---

### 📥 Fetching the Android 13.0.0_r35 source

1. Create a working directory:
```bash
mkdir android-13-r35
cd android-13-r35
```

2. Initialize the repo:
```bash
repo init -u https://android.googlesource.com/platform/manifest -b android-13.0.0_r35
```

3. Sync the source (this may take a while depending on your internet speed):
```bash
repo sync -c -j$(nproc)
```

---

### 🧾 Verify the version (optional)

To confirm the branch or check the manifest, you can run:
```bash
grep -r "platform/build" .repo/manifests/
```

---

Let me know if you’d like help compiling for a specific device or generating a GSI image!