## Data assumptions and inconsistencies
I found one patient in the supplied data who has a discharge date but no registration date. 
I therefore did not assume that the lifecycle dates would always be complete.
For the dashboard I derive the current status by checking discharge first, then registration, then invitation. 
This also means each patient belongs to one status only.