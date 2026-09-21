
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
    'index.csr.html': {size: 11928, hash: 'b62ef91643bcb734397f2ee79c19c6a6017dc8206330cdc74f571e69249c0108', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 12114, hash: '5c22b3178d6683ce8c8190461980ec3ec978e4f952f91b279d6f32fdc4beb938', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'index.html': {size: 28251, hash: 'cdb414827d12218ad608aead7033502e28c62decdcfebfafd34fd96d2e3afbd5', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'styles-HXK7QCQ7.css': {size: 202, hash: 'dVeOGgstMB0', text: () => import('./assets-chunks/styles-HXK7QCQ7_css.mjs').then(m => m.default)}
  },
};
