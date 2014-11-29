/**********************************/
/* テーブル名: メトリクス */
/**********************************/
DROP TABLE CPUMetrics;
CREATE TABLE CPUMetrics (
	HostName			VARCHAR(30),
	PortNumber          INTEGER,
	AcquiredTimeStamp	TIMESTAMP,
	CoreName			VARCHAR(10),
	MetricName			VARCHAR(10),
	MetricValue			DOUBLE,
	CONSTRAINT CPUMetricsPrimaryKeys PRIMARY KEY(HostName,PortNumber, AcquiredTimeStamp,
	CoreName, MetricName)
);