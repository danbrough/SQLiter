#!/bin/bash
cd `dirname "$0"`

TASKS="publishAllPublicationsToMavenRepository"

if [ "$(uname)" = "Darwin" ]; then
	TASKS="$(cat mac_targets.txt)"
fi 

./gradlew  \
 -PRELEASE_REPOSITORY_URL=https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$SONATYPE_REPO_ID \
 -PSONATYPE_NEXUS_USERNAME=$SONATYPE_USER -PSONATYPE_NEXUS_PASSWORD=$SONATYPE_PASSWORD \
  $TASKS


