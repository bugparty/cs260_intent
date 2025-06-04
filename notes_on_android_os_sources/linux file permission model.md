In Linux, r, w, x, d are the most common permission flags we encounter when operating files and directories. Below, I will break them down and explain some points that are easy to confuse:

---
## **🧾 Overview of Linux File Permissions**

### **Three Types of File Permissions (Subjects):**

|**Permission Target**|**English**|**Explanation**|
|---|---|---|
|u|**user** (file owner)||
|g|**group** (file's group)||
|o|**others** (everyone else)||

You can see the following format using `ls -l`:

```
-rw-r--r--  1 bowen users 1234 Apr 16 test.txt
```

This indicates:

- user (you) has rw permissions
    
- group members have r permissions
    
- others (everyone else) also have r permissions
    

---
## **🔤 Detailed Explanation of Permission Symbols**

|**Symbol**|**English**|**Meaning**|
|---|---|---|
|r|**read**|Can read file contents / list directory contents|
|w|**write**|Can modify file contents / create, delete, rename files in the directory|
|x|**execute**|Can execute the file / enter the directory|
|d|**directory flag**|Indicates the file is a **directory**, not a permission but a type! (Be careful not to confuse)|


---

## **📂 Differences Between File vs Directory Permissions**

### **▶️ File Permission Behavior:**

| **Permission** | **Meaning**                   |
| ------ | ------------------------ |
| r      | Can use `cat`, `less` to read the file       |
| w      | Can use `echo >>`, `nano` to edit or overwrite the file |
| x      | Can directly execute the file (e.g., .sh, executable ELF) |

### **📁 Directory Permission Behavior:**

|**Permission**|**Meaning**|
|---|---|
|r|Can **view directory contents** (ls)|
|w|Can **create, delete, rename files in the directory**|
|x|Can **enter the directory** (cd into) or access specific files within the directory|

📌 **Note:** Access permissions for directories are easily misunderstood:

- If you don't have x permission, you cannot cd into the directory, even if you have r.
    
- If you don't have r permission, you cannot ls the files, even if you can enter (have x).
    

---

## **🔐 Example:**

## **drwxr-x--x**

  

These permissions mean:

|**Target**|**Permissions**|
|---|---|
|user|read, write, execute (can view, enter, modify the directory)|
|group|read, execute (can view, enter, but cannot create/delete files)|
|others|execute (can enter the directory, but cannot see ls contents)|

  

---

## **🧠 Common Misunderstandings:**

1. x does not always mean "executable", **for directories, it means "enter"**
    
2. d is not a permission, but a file type (Directory)
    
3. Use chmod to change permissions, chown to change owner, and chgrp to change group
    
4. w permission has a significant impact on directories, such as the ability to delete files (even if the files themselves are read-only)
    

---

Would you like me to create a small table or command practice exercises to help reinforce your memory? Or if you have actual permission issues you've encountered, I can help analyze them as well～🧑‍💻