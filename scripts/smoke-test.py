#!/usr/bin/env python3
import json
import sys
from urllib.error import HTTPError
from urllib.request import Request, urlopen

base = sys.argv[1] if len(sys.argv) > 1 else 'http://127.0.0.1:8080'


def request(path, tenant='tenant1', body=None, expected=200):
    headers = {'Host': f'{tenant}.localhost', 'Content-Type': 'application/json'}
    payload = json.dumps(body).encode() if body is not None else None
    req = Request(base + path, data=payload, headers=headers)
    try:
        response = urlopen(req, timeout=15)
    except HTTPError as error:
        response = error
    with response:
        assert response.status == expected, (path, tenant, response.status, response.read())
        return json.load(response)


assert request('/actuator/health')['status'] == 'UP'
before = {}
for tenant in ('tenant1', 'tenant2'):
    assert request('/api/me', tenant)['tenantId'] == tenant
    rows = request('/api/expenses', tenant)
    assert rows and all(row['tenantId'] == tenant for row in rows)
    before[tenant] = request('/api/expenses/total', tenant)
    assert before[tenant]['count'] == len(rows)
    assert before[tenant]['total'] == sum(row['amount'] for row in rows)

expense = request('/api/expenses', body={
    'description': 'Smoke test', 'amount': 12.5, 'spentOn': '2026-09-21',
    'tenantId': 'tenant2',
}, expected=201)
assert expense['tenantId'] == 'tenant1'
assert request(f"/api/expenses/{expense['id']}")['id'] == expense['id']
request(f"/api/expenses/{expense['id']}", 'tenant2', expected=404)
assert request('/api/expenses/total', 'tenant2') == before['tenant2']
after = request('/api/expenses/total')
assert after['count'] == before['tenant1']['count'] + 1
assert after['total'] == before['tenant1']['total'] + 12.5

for tenant in ('tenant1', 'tenant2') * 5:
    totals = request('/api/expenses/total', tenant)
    raw = request('/api/debug/leak-test', tenant)
    assert raw['tenantId'] == raw['postgresSessionTenant'] == tenant
    assert raw['rowsVisible'] == totals['count']
    assert raw['sumVisible'] == totals['total']

request('/api/expenses', 'nobody', expected=404)
try:
    urlopen(base + '/api/expenses', timeout=15)
    raise AssertionError('Request without a tenant was accepted')
except HTTPError as error:
    assert error.code == 404

print('Passed: health, tenant lists and totals, create/read isolation, RLS, tenant switching, and invalid tenants.')
