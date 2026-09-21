
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
    'index.csr.html': {size: 11928, hash: '935da5ed8d833ed56dbb99c4296635a59d6a16a24009aa453316938ceb921002', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 12114, hash: '72ed794844d41eb978bb7449fa23dfaca9415d1dbb291c5322466082b1fb3903', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'index.html': {size: 28251, hash: '0375a88cd3ac83289391213176038d691c07b27fc36637483f6feac41109dc16', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'styles-HXK7QCQ7.css': {size: 202, hash: 'dVeOGgstMB0', text: () => import('./assets-chunks/styles-HXK7QCQ7_css.mjs').then(m => m.default)}
  },
};
