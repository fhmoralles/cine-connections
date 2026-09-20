
export default {
  bootstrap: () => import('./main.server.mjs').then(m => m.default),
  inlineCriticalCss: false,
  baseHref: '/',
  locale: undefined,
  routes: [
  {
    "renderMode": 2,
    "route": "/"
  },
  {
    "renderMode": 1,
    "route": "/movie/*"
  },
  {
    "renderMode": 1,
    "route": "/person/*"
  },
  {
    "renderMode": 2,
    "redirectTo": "/",
    "route": "/**"
  }
],
  entryPointToBrowserMapping: undefined,
  assets: {
    'index.csr.html': {size: 654, hash: '3f4096f652cef6557bf5c87cfe8053819c12efd7aa260ae98a22d32d5c4785ef', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 1194, hash: 'cedd8aa1f5970a24ba50bc30845c00ea7d46cccb3e0407584f98d760a3d80a4c', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'index.html': {size: 13969, hash: 'c7671d9fe82b81970728d648e0155f4518ef9b5d17b9ccc55db8075e8c194369', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)}
  },
};
