/**********************************/
/* テーブル名: メトリクス */
/**********************************/
DROP TABLE IoStatMetrics;
CREATE TABLE IoStatMetrics (
	HostName			VARCHAR(30),
	PortNumber          INTEGER,
	AcquiredTimeStamp	TIMESTAMP,
	DeviceName			VARCHAR(10),
	MetricName			VARCHAR(10),
	MetricValue			DOUBLE,
	CONSTRAINT IoStatMetricsPrimaryKeys PRIMARY KEY(HostName,PortNumber, AcquiredTimeStamp,
	DeviceName, MetricName)
);