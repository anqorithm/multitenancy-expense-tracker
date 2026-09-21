create table tenants (
  slug       text primary key,
  name       text not null,
  created_at timestamptz not null default now()
);
