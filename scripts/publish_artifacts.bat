@echo off
rem ----------------------------------------------------------------------------
rem - prior to running this script need to set the following Environment Variables :
rem -  EV_signing_keyId
rem -  EV_signing_password
rem -  EV_ossrhUsername
rem -  EV_ossrhPassword
rem ----------------------------------------------------------------------------

set scriptDir=%~dp0

pushd %scriptDir%\..

rem make sure that publish runs in NO parallel mode, since it might create multiple repositories on the OSS server
gradlew publish --no-parallel ^
 -Psigning.secretKeyRingFile=%cd%/xtra/security/secr-key-ring-file-gpg.bin ^
 -Psigning.password=%EV_signing_password% ^
 -Psigning.keyId=%EV_signing_keyId%

popd
