#!/bin/bash
cd `dirname "$0"`

./gradlew publishAllPublicationsToMavenRepository \
 -PRELEASE_REPOSITORY_URL=https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$SONATYPE_REPO_ID \
 -PSONATYPE_NEXUS_USERNAME=$SONATYPE_USER -PSONATYPE_NEXUS_PASSWORD=$SONATYPE_PASSWORD

