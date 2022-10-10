#!/bin/bash
cd `dirname "$0"`

TASKS="publishAllPublicationsToSonaTypeRepository"
[ -z "$SONATYPE_USER" ] && echo SONATYPE_USER not set && exit 1
[ -z "$SONATYPE_PASSWORD" ] && echo SONATYPE_PASSWORD not set && exit 1
[ -z "$SONATYPE_REPO_ID" ] && echo SONATYPE_REPO_ID not set && exit 1


OPTS="-PRELEASE_REPOSITORY_URL=https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$SONATYPE_REPO_ID \
 -PSONATYPE_NEXUS_USERNAME=$SONATYPE_USER -PSONATYPE_NEXUS_PASSWORD=$SONATYPE_PASSWORD"

if [ "$(uname)" = "Darwin" ]; then
	TASKS="$(cat mac_targets.txt)"
fi

./gradlew $OPTS $TASKS


