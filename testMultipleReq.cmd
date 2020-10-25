REM For pdf test change data with RDpcV29ya3NwYWNlXGNvbnZlcnRlci1zZXJ2aWNlLWhhcmZcZmlsZXN0b3JhZ2VcaW5wdXRzXGRlbmVtZS5wZGY
REM For docx test change data with RDpcV29ya3NwYWNlXGNvbnZlcnRlci1zZXJ2aWNlLWhhcmZcZmlsZXN0b3JhZ2VcaW5wdXRzXGRlbmVtZS5kb2N4
REM For pptx test change data with RDpcV29ya3NwYWNlXGNvbnZlcnRlci1zZXJ2aWNlLWhhcmZcZmlsZXN0b3JhZ2VcaW5wdXRzXGRlbmVtZS5wcHR4
REM OLD VERSION : curl  --data "RDpcV29ya3NwYWNlXGNvbnZlcnRlci1zZXJ2aWNlLWhhcmZcZmlsZXN0b3JhZ2VcaW5wdXRzXGRlbmVtZS5wcHR4" 127.0.0.1:9090/convert
set nIterations=20

FOR /L %%A IN (1,1,%nIterations%) DO (
	ECHO Starting %%A
	start cmd /c curl -X POST -F 'filePath=RDpcV29ya3NwYWNlXGNvbnZlcnRlci1zZXJ2aWNlLWhhcmZcZmlsZXN0b3JhZ2VcaW5wdXRzXGRlbmVtZS5wcHR4' http://localhost:9090/convert
)

ECHO TEST FOR %nIterations% is DONE.