#!/bin/sh
java -jar signapk.jar platform.x509.pem platform.pk8  /externd2/workspace/rk30/jb/out/target/product/rk30sdk/system/app/JNSIME.apk /externd2/workspace/rk30/jb/out/target/product/rk30sdk/system/app/JNSIME_out.apk
rm /externd2/workspace/rk30/jb/out/target/product/rk30sdk/system/app/JNSIME.apk
cp /externd2/workspace/rk30/jb/out/target/product/rk30sdk/system/app/JNSIME_out.apk /externd2/workspace/rk30/jb/out/target/product/rk30sdk/system/app/JNSIME.apk
rm /externd2/workspace/rk30/jb/out/target/product/rk30sdk/system/app/JNSIME_out.apk
