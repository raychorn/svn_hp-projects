import logging

logging.VERBOSE = 5
logging.addLevelName(logging.VERBOSE, 'VERBOSE')

    
def Logger_verbose(self, msg, *args, **kwargs):    
    if self.manager.disable >= logging.VERBOSE:
        return
    if logging.VERBOSE >= self.getEffectiveLevel():
        apply(self._log, (logging.VERBOSE, msg, args), kwargs)

# make the verbose function known in the system Logger class
logging.Logger.verbose = Logger_verbose

# define a new root level verbose function
def root_verbose(msg, *args, **kwargs):
    """
    Log a message with severity 'VERBOSE' on the root logger.
    """
    if len(root.handlers) == 0:
        basicConfig()
    apply(root.verbose, (msg,)+args, kwargs)

# make the verbose root level function known
logging.verbose = root_verbose

# add VERBOSE to the priority map of all the levels
#logging.handlers.SysLogHandler.priority_map['VERBOSE'] = 'verbose'