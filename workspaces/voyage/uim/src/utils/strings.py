#####################################################
#
# strings.py
#
# Copyright 2011 Hewlett-Packard Development Company, L.P.
#
# Hewlett-Packard and the Hewlett-Packard logo are trademarks of
# Hewlett-Packard Development Company, L.P. in the U.S. and/or other countries.
#
# Confidential computer software. Valid license from Hewlett-Packard required
# for possession, use or copying. Consistent with FAR 12.211 and 12.212,
# Commercial Computer Software, Computer Software Documentation, and Technical
# Data for Commercial Items are licensed to the U.S. Government under
# vendor's standard commercial license.
#
# Author:
#    James Abendroth
# 
# Description:
#    String files util functions
#
#####################################################

import os
import json
import codecs

str_mod_dates = {}

def merge_strings(language):
    strings_dir = 'static/js/i18n/%s' % (language)
    filenames = [f for f in os.listdir(strings_dir) if f not in ('.svn', '%s.js' % (language))]
    js_str = ''
    for file in filenames:
        try:
            filename = "%s/%s" % (strings_dir, file)
            f = open(filename, 'r')
            js_str += f.read()

            st = os.stat(filename)
            # The value in the 8th position of the tuple is the date modified.
            str_mod_dates[filename] = st[8]
        except:
            pass

    f = open("%s/%s.js" % (strings_dir, language), 'w')
    f.write(js_str)
    #json.dump(strings, codecs.open("%s/%s" % (strings_dir, 'strings.json'), "w", "utf-8"))

# Checks to see if the strings files have been modified since they were first merged.
def check_strings_current(language):
    strings_dir = 'static/js/i18n'

    # First check to see if we have the combined file. If not, we are obviously out of date.
    try:
        st = os.stat('%s/%s.js' % (strings_dir, language))
    except:
        return False

    filenames = [f for f in os.listdir(strings_dir) if f not in ('.svn', '%s.js' % language)]
    for file in filenames:
        filename = "%s/%s" % (strings_dir, file)
        if filename not in str_mod_dates:
            return False
        st = os.stat(filename)
        # The value in the 8th position of the tuple is the date modified.
        if st[8] != str_mod_dates[filename]:
            return False
    return True
