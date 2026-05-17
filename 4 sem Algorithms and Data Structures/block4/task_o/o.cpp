#include <bits/stdc++.h>
using namespace std;

int main() {
  int n, m;
  cin >> n >> m;

  vector<int> g[105];
  int a, b;

  for (int i = 0; i < m; i++) {
    cin >> a >> b;
    g[a].push_back(b);
    g[b].push_back(a);
  }

  int c[105];
  memset(c, -1, sizeof(c));

  queue<int> q;

  for (int i = 1; i <= n; i++) {
    if (c[i] != -1)
      continue;

    c[i] = 0;
    q.push(i);

    while (!q.empty()) {
      int u = q.front();
      q.pop();

      for (int v : g[u]) {
        if (c[v] == -1) {
          c[v] = c[u] ^ 1;
          q.push(v);
        } else if (c[v] == c[u]) {
          cout << "NO";
          return 0;
        }
      }
    }
  }

  cout << "YES";
}