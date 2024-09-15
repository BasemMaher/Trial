package com_2is.egypt.wipegadmin.entites

import java.lang.Exception

class FailedToUpdateDatabaseException(differenceCount: Int) : Exception("$differenceCount not Saved")
