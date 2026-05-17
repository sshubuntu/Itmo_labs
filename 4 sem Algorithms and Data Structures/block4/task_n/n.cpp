#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> a;
vector<int> v;

int main() {
  cin >> n;

  a.resize(n + 1);
  for (int i = 1; i <= n; i++)
    cin >> a[i];

  v.assign(n + 1, 0);

  int c = 0;

  for (int i = 1; i <= n; i++) {
    if (v[i])
      continue;

    int x = i;

    while (!v[x]) {
      v[x] = i;
      x = a[x];
    }

    if (v[x] == i)
      c++;
  }

  cout << c;

  return 0;
}