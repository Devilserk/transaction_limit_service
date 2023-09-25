CREATE KEYSPACE IF NOT EXISTS spring_cassandra
  WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : 1
  };

CREATE TABLE IF NOT EXISTS spring_cassandra.exchange_rates (
  currency_pair TEXT PRIMARY KEY,
  close_rate DECIMAL,
  date DATE
);

