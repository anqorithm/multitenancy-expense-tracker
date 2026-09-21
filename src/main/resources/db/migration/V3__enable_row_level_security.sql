alter table expenses enable row level security;

alter table expenses force row level security;

create policy tenant_isolation on expenses
  using      (tenant_id = current_setting('app.tenant_id', true))
  with check (tenant_id = current_setting('app.tenant_id', true));
