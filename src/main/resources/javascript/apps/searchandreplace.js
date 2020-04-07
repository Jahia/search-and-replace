window.jahia.i18n.loadNamespaces('search-and-replace');

window.jahia.uiExtender.registry.add('adminRoute', 'search-and-replace', {
    targets: ['jcontent:30'],
    label: 'search-and-replace:label.title',
    icon: window.jahia.moonstone.toIconComponent('Search'),
    isSelectable: true,
    requiredPermission: 'editSearchAndReplace',
    requireModuleInstalledOnSite: 'search-and-replace',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.searchAndReplace.html'
});
