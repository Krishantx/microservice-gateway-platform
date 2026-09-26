import http from 'k6/http';
import { check } from 'k6';
import { Counter, Trend } from 'k6/metrics';

export const options = {
  vus: 100,
  duration: '60s',
  thresholds: {
    http_req_duration: ['p(95)<500'],
    checks: ['rate>0.99'],
  },
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)'],
};

const TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrcmlzaGFudHgiLCJpYXQiOjE3OTA0MDMwOTIsImV4cCI6MTc5MDQwNDg5Mn0.Mmn08yqVJcz3hRr9N597Ip2QF3d3b2R5RMQJyrcdSgE';
const API_KEY = 'GXDekhdd1Qtl5eMiJhxtdaplcS-GjSa5ADLgpbyEyc0';
const BASE_URL = 'http://localhost:8080';

const authenticatedReqs = new Counter('authenticated_requests');
const authenticatedLatency = new Trend('authenticated_latency');
const rejectedReqs = new Counter('rejected_requests');
const rejectedLatency = new Trend('rejected_latency');
const gatewayErrors = new Counter('gateway_errors');

export default function () {
  const authorized = Math.random() < 0.8;

  const res = http.get(`${BASE_URL}/profile`, {
    headers: {
      Authorization: `Bearer ${authorized ? TOKEN : 'invalid.token.here'}`,
      'x-api-key': API_KEY,
    },
  });

  if (authorized) {
    if (res.status === 200) {
      authenticatedReqs.add(1);
      authenticatedLatency.add(res.timings.duration);
    } else {
      gatewayErrors.add(1);
    }
    check(res, {
      'authorized request returns 200': (r) => r.status === 200,
    });
  } else {
    if (res.status === 401) {
      rejectedReqs.add(1);
      rejectedLatency.add(res.timings.duration);
    } else {
      gatewayErrors.add(1);
    }
    check(res, {
      'unauthenticated request returns 401': (r) => r.status === 401,
    });
  }
}