create table expenses (
  id          bigserial primary key,
  tenant_id   text not null references tenants (slug),
  description text not null,
  amount      numeric(12, 2) not null check (amount >= 0),
  spent_on    date not null,
  created_at  timestamptz not null default now()
);

create index expenses_tenant_id_idx on expenses (tenant_id);
