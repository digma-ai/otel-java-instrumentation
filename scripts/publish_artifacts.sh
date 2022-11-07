#!/bin/bash
# ------------------------------------------------------------------------------
# - prior to running this script need to set the following Environment Variables :
# -  EV_signing_keyId
# -  EV_signing_password
# -  EV_ossrhUsername
# -  EV_ossrhPassword
# ------------------------------------------------------------------------------

scriptRelPath=$(dirname $0)

pushd $scriptRelPath/.. > /dev/null

./gradlew clean   --parallel

# make sure that publish runs in NO parallel mode, since it might create multiple repositories on the OSS server
./gradlew publish --no-parallel \
 -Psigning.secretKeyRingFile=`pwd`/xtra/security/secr-key-ring-file-gpg.bin \
 -Psigning.password=$EV_signing_password \
 -Psigning.keyId=$EV_signing_keyId

popd > /dev/null
