#!/bin/sh

###### PLACEHOLDER HYDRATION SCRIPT ######
#
# This script will modify the image's JavaScript 
# files on docker run command to hydrate the 
# placeholder environment variables on run-time.
# Env variables passed docker run command as
# -e PLACEHOLDER_<VALUE>=<ACTUAL_VALUE>
# will be picked by this script, and the actual
# values will be used for hydration.
# -----------------------------------------------
# Refer to the source article: 
# https://pamalsahan.medium.com/dockerizing-a-react-application-injecting-environment-variables-at-build-vs-run-time-d74b6796fe38
#
#################################################

echo 'STARTING PLACEHOLDER ENV HYDRATION...'
for i in $(env | grep PLACEHOLDER_)
do
    key=$(echo $i | cut -d '=' -f 1)
    value=$(echo $i | cut -d '=' -f 2-)
    echo $key=$value
    # sed All files
    # find /usr/share/nginx/html -type f -exec sed -i "s|${key}|${value}|g" '{}' +

    # sed JS and CSS only
    find /usr/share/nginx/html -type f -name '*.js' -exec sed -i "s|${key}|${value}|g" '{}' +
done
echo 'PLACEHOLDER ENV HYDRATION COMPLETED!'