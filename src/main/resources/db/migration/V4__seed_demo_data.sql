insert into tenants (slug, name) values
  ('tenant1', 'Tenant One Inc'),
  ('tenant2', 'Tenant Two Ltd');

select set_config('app.tenant_id', 'tenant1', true);
insert into expenses (tenant_id, description, amount, spent_on) values
  ('tenant1', 'Office rent',     5000.00, '2026-09-01'),
  ('tenant1', 'Laptops',        12000.00, '2026-09-05'),
  ('tenant1', 'Coffee',           150.00, '2026-09-10');

select set_config('app.tenant_id', 'tenant2', true);
insert into expenses (tenant_id, description, amount, spent_on) values
  ('tenant2', 'Truck fuel',       800.00, '2026-09-03'),
  ('tenant2', 'Warehouse rent',  2300.00, '2026-09-08');
