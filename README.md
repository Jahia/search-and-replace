# Search and Replace

## Overview
The Search and Replace module provide an interface to search and replace a term on the site where the module is deployed.
 It is accessible on the site settings section in Edit Mode under the name `Search & Replace`. Using this module any user with the `editSearchAndReplace` permission
 will be able to search and replace in the Digital Factory site.

## Goals

- Provide a simple and fast way to search and replace content on a Digital Factory site.
- Provide the possibility to filter you result. (by date created/modified, type and field)
- Provide the possibility to change only one field of the current node.
- Provide the possibility to choose different matching.
- Expose an OSGi service, who permit the user to call different method to execute search and replace on nodes from another module.

## Version

Search and Replace v1.0.0

---

## TODO
- Possibility to choose the matching for the research.
- Adapt the query to return only available result for the current user.
- Define the authorization access to the module. (current only site admin).

---

## Presentation
### First View
When you arrived on the Search & Replace the first view is a simple search field, who is of course mandatory so it can't be empty.
The field search can receive every word or sentence you are looking for, but keep in mind that the research tool is currently
 only an exact match so it is case sensitive, more option will be available in the next version.
3 cases :

- If you start a research and the term you are looking for is found, you will be send on the second view.
- If you start a research and the term you are looking for is not find you will be resend on this view with an information message.
- If you start a research with and empty field you will stay on this view and an error message will be show.

### Second View
At this point the module shows you the result of your request, here you will be able to perform 4 actions :
1. visualize each nodes where the term is found, by clicking on the node name. (a modal will be show)
2. Three possible selection :
  * select one node to update then click on “Next”
  * Select several nodes by using the checkboxes on the first column then click on “Next”
  * Or select all the nodes, by using the main checkbox on the top of the first column to select all the nodes then click on “Next”
3. perform an advanced search by clicking on the “Advanced Search” button
  * Here you will be able to select different option for your research like the node type or the date of creation/modification.
  * You can fill all the field or only one, each time you will fill a field a new request will be made and a new result corresponding to your new research will be display.
  * The field “field(s)” will show you different options only if you first fill the field “Node type”.
4. click on the “Cancel” button to back to the first view and start another research

### Third View
On this view you will be able to perform the replacement, the view should display one of the nodes from the previous step, and two fields.
The first field `Term to replace` is not editable, it is basically the term you were looking for. The second field `Replace With`, will contain the new term (this field can be left empty if needed).
Depending of the number of nodes you’ve selected on the previous page the view can have 2 or 4 buttons so 2 or 4 actions :
1. `Skip this node` no change will be performed on the node and depending on the number of nodes you’ve selected you will be sent to the next available node or to the first view with a summary of the action you’ve performed on the different nodes.
2. `Skip all the nodes` will skip all the nodes you selected and still available, you will be send to the first view with a summary of the action you performed on the different node.
3. `Replace all in this node` will replace all the occurrences in the current displayed node and depending on the number of nodes you’ve selected you will be sent to the next available node or to the first view with a summary of the action you’ve performed on the different nodes.
4. `Replace in all nodes` will replace all the occurrences in the nodes you’ve selected and that are still available, you will be sent to the first view with a summary of the action you’ve performed on the different nodes.