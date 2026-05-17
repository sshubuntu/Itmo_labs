#include <bits/stdc++.h>
using namespace std;

int main() {
  int n, m, r, c, x, y;
  cin >> n >> m >> r >> c >> x >> y;
  r--, c--, x--, y--;

  vector<string> g(n);
  for (auto& s : g)
    cin >> s;

  const long long I = 1e18;
  vector d(n, vector(m, I));
  vector p(n, vector<pair<int, int>>(m, {-1, -1}));

  priority_queue<tuple<long long, int, int>, vector<tuple<long long, int, int>>, greater<>> q;

  d[r][c] = 0;
  q.push({0, r, c});

  int dr[] = {-1, 0, 1, 0};
  int dc[] = {0, 1, 0, -1};

  while (!q.empty()) {
    auto [w, i, j] = q.top();
    q.pop();
    if (w > d[i][j])
      continue;
    for (int k = 0; k < 4; k++) {
      int ni = i + dr[k], nj = j + dc[k];
      if (ni < 0 || ni >= n || nj < 0 || nj >= m || g[ni][nj] == '#')
        continue;
      long long nw = w + (g[ni][nj] == 'W' ? 2 : 1);
      if (nw < d[ni][nj]) {
        d[ni][nj] = nw;
        p[ni][nj] = {i, j};
        q.push({nw, ni, nj});
      }
    }
  }

  if (d[x][y] == I) {
    cout << -1;
    return 0;
  }

  cout << d[x][y] << '\n';

  string s;
  for (int i = x, j = y; i != r || j != c;) {
    auto [pi, pj] = p[i][j];
    if (pi == i - 1)
      s += 'S';
    else if (pi == i + 1)
      s += 'N';
    else if (pj == j - 1)
      s += 'E';
    else
      s += 'W';
    i = pi;
    j = pj;
  }

  reverse(s.begin(), s.end());
  cout << s;
}