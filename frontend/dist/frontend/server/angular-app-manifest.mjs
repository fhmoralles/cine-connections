
export default {
  bootstrap: () => import('./main.server.mjs').then(m => m.default),
  inlineCriticalCss: true,
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
    'index.csr.html': {size: 11928, hash: 'fff57229cbb942a797ec7fd5e6458da30202b37c04311883976175c19b197126', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 12114, hash: '2cf425015b57dee9683d4f4a14cd4ee3ca059731062ecd6bb49d10feb4f27b25', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'index.html': {size: 28452, hash: '5cdb86ebd1bbd729c1d2acf715f48e1c43a38ef694e6ef63366596ce9d14597d', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'styles-HXK7QCQ7.css': {size: 202, hash: 'dVeOGgstMB0', text: () => import('./assets-chunks/styles-HXK7QCQ7_css.mjs').then(m => m.default)}
  },
};
