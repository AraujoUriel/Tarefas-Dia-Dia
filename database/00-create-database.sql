-- Executar como administrador do PostgreSQL, conectado ao banco "postgres".
-- Execute as instrucoes fora de uma transacao.
-- Credenciais apenas para desenvolvimento local.
CREATE USER todo WITH PASSWORD 'todo';
CREATE DATABASE todo OWNER todo;
-- Depois conecte ao banco todo com o usuario todo e execute 01-schema.sql.