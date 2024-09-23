#!/bin/bash

source .env

if [ -z "$JOWL_PATH" ]; then
    PROPERTIES_FILE="src/main/resources/application.properties"
else
    PROPERTIES_FILE="$JOWL_PATH/src/main/resources/application.properties"
fi

# Update application.properties
{
    echo "virtuoso.password=$VIRTUOSO_PASSWORD"
    echo "virtuoso.user=$VIRTUOSO_USER"
    echo "virtuoso.endpoint=$VIRTUOSO_ENDPOINT"
} > "$PROPERTIES_FILE"

echo "application.properties has been updated successfully."
