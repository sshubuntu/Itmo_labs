#include <bits/stdc++.h>
using namespace std;

int n;
long long a[1001][1001];
int u[1001];

void dfs(int v, long long x) {
  u[v] = 1;
  for (int i = 1; i <= n; i++)
    if (!u[i] && a[v][i] <= x)
      dfs(i, x);
}

void rdfs(int v, long long x) {
  u[v] = 1;
  for (int i = 1; i <= n; i++)
    if (!u[i] && a[i][v] <= x)
      rdfs(i, x);
}

bool ok(long long x) {
  memset(u, 0, sizeof(u));
  dfs(1, x);
  for (int i = 1; i <= n; i++)
    if (!u[i])
      return false;

  memset(u, 0, sizeof(u));
  rdfs(1, x);
  for (int i = 1; i <= n; i++)
    if (!u[i])
      return false;

  return true;
}

int main() {
  cin >> n;

  for (int i = 1; i <= n; i++)
    for (int j = 1; j <= n; j++)
      cin >> a[i][j];

  long long l = 0, r = 1e9;

  while (l < r) {
    long long m = (l + r) / 2;
    if (ok(m))
      r = m;
    else
      l = m + 1;
  }

  cout << l;
}