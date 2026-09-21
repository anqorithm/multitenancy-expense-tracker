create role app login password 'app' nosuperuser nocreatedb nocreaterole;
alter database multitenancy owner to app;
