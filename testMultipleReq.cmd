set nIterations=20

FOR /L %%A IN (1,1,%nIterations%) DO (
	ECHO Starting %%A
	start cmd /c curl  --data "RDpcV29ya3NwYWNlXGNvbnZlcnRlci1zZXJ2aWNlLWhhcmZcZmlsZXN0b3JhZ2VcaW5wdXRzXGRlbmVtZS5wcHR4" 127.0.0.1:9090/convert 
)

ECHO TEST FOR %nIterations% is DONE.