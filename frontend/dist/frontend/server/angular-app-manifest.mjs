
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
    'index.csr.html': {size: 11928, hash: '85c7f3697b3bf9ce6a9edf0bbdb4e3ee61bdc1d9f8b0c7ce11f6f7edaf759d84', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 12114, hash: '10793bcacfaab064cbe8566a6c46613e60fbe2fe1aface67b9c830bf3858bfa1', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'index.html': {size: 28251, hash: 'd6012f6c0b5c693f83a56fd1e5259a6776b2a5d7de05b69ef0b6cc9ea36f9b5f', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'styles-HXK7QCQ7.css': {size: 202, hash: 'dVeOGgstMB0', text: () => import('./assets-chunks/styles-HXK7QCQ7_css.mjs').then(m => m.default)}
  },
};
