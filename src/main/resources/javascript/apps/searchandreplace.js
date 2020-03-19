window.jahia.i18n.loadNamespaces('search-and-replace');

window.jahia.uiExtender.registry.add('adminRoute', 'search-and-replace', {
    targets: ['jcontent:50'],
    label: 'search-and-replace:label.title',
    icon: null,
    isSelectable: true,
    requireModuleInstalledOnSite: 'search-and-replace',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/en/sites/$site-key.searchAndReplace.html'
});
