%1 mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c %~s0 ::","","runas",1)(window.close)&&exit
cd /d "%~dp0"
copy .\rxtxParallel.dll C:\Windows\System32\rxtxParallel.dll
copy .\rxtxSerial.dll C:\Windows\System32\rxtxSerial.dll

