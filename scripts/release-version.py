#!/usr/bin/env python3
import re
import sys
import xml.etree.ElementTree as ET

version = ET.parse('pom.xml').getroot().findtext('{http://maven.apache.org/POM/4.0.0}version')
tag = sys.argv[1]
if not re.fullmatch(r'v(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)', tag):
    sys.exit('Release tags must use vMAJOR.MINOR.PATCH, for example v0.1.0.')
if tag != f'v{version}':
    sys.exit(f'Tag {tag} does not match pom.xml version {version}.')
print(version)
