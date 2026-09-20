
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
    'index.csr.html': {size: 11928, hash: '43fa098dcdecc1220bbb328cfb297008707463b0561d8ff50686437f85a00e1c', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 12114, hash: 'a13cbca22c04e0bc1edcbbe8bed08e0f914790439c2e70e11641e1ef8f9675ca', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'index.html': {size: 28251, hash: '95ab364ce6bdb6c5c3c2e1d56bc9f15d7399a89f3eef619e88344cac7c7b375f', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'styles-HXK7QCQ7.css': {size: 202, hash: 'dVeOGgstMB0', text: () => import('./assets-chunks/styles-HXK7QCQ7_css.mjs').then(m => m.default)}
  },
};
