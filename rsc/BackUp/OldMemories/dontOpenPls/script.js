function openSidebar() {
  document.getElementById("main").style.marginLeft = "100pt";
  document.getElementById("sidebar").style.width = "100pt";
  document.getElementById("sidebar").style.display = "block";
  document.getElementById("openNav").style.display = 'none';
}

function closeSidebar() {
  document.getElementById("main").style.marginLeft = "0%";
  document.getElementById("sidebar").style.display = "none";
  document.getElementById("openNav").style.display = "inline-block";
}

async function loadAll() {
  console.log("erger");
  //get the file
  const response = await fetch("/rsc/commands.json");
  const aaa = await response.json();
  var json = JSON.parse(JSON.stringify(aaa));
  //put all the commands in a map grouped by category
  let commands = new Map();
  for (let key in json) {
    if (!commands.has(json[key]["category"])) {
      commands.set(json[key]["category"], new Array());
    }
    commands.get(json[key]["category"]).push(key);
  }
  let containerCommands = document.getElementsByClassName("outside-command-container")[0];
  lastOpen = containerCommands;
  //create the html div for each category and iterate over categories
  let keys = Array.from(commands.keys());
  for (var key in keys) {
    var category = document.createElement("div");
    var h1 = document.createElement("h1");
    h1.className = "category-name";
    category.className = "category-container";
    let commandsContainer = document.createElement("div");
    commandsContainer.className = "commands-container";

    h1.innerHTML = keys[key];
    category.appendChild(h1);
    containerCommands.appendChild(category);
    category.appendChild(commandsContainer);
    //iterate over the commands of the category "key"
    //name = command's name
    commands.get(keys[key]).forEach(function (name) {
      let commandCard = document.createElement("div");
      commandCard.className = "command-card";
      command = json[name];
      var button = document.createElement("button");
      var content = document.createElement("div");
      var table = document.createElement("table");
      var tr = document.createElement("tr");
      button.innerHTML = name;
      button.className = "collapsible";
      commandCard.appendChild(button);
      content.className = "content";
      content.style = "display:none";
      table.className = "table";
      table.style = "display:none";
      tr.className = "top";
      var td1 = document.createElement("td");
      var td2 = document.createElement("td");
      var td3 = document.createElement("td");
      var td4 = document.createElement("td");
      var td5 = document.createElement("td");
      td1.style = "border-radius: 15px 0px 0px 0px; border-top:0px;";
      td2.style = "border-top:0px;";
      td3.style = "border-top:0px;";
      td4.style = "border-top:0px;";
      td5.style = "border-radius: 0px 15px 0px 0px; border-top:0px;";
      td1.innerHTML = "Help";
      td2.innerHTML = "Arguments";
      td3.innerHTML = "Category";
      td4.innerHTML = "Aliases";
      td5.innerHTML = "Cooldown";
      tr.appendChild(td1);
      tr.appendChild(td2);
      tr.appendChild(td3);
      tr.appendChild(td4);
      tr.appendChild(td5);
      table.appendChild(tr);
      commandCard.appendChild(table);
      var tr = document.createElement("tr");
      var td1 = document.createElement("td");
      var td2 = document.createElement("td");
      var td3 = document.createElement("td");
      var td4 = document.createElement("td");
      var td5 = document.createElement("td");
      tr.className = "inside";
      td1.style = "border-radius: 0px 0px 0px 15px;";
      td5.style = "border-radius: 0px 0px 15px 0px;";
      td1.innerHTML = command["help"];
      td2.innerHTML = command["arguments"];
      td3.innerHTML = command["category"];
      //iterate all the aliases
      var aliases = command["alias"];
      var alias = "";
      for (var i = 0; i < aliases.length; i++) {
        alias += aliases[i];
        if (i != aliases.length - 1) {
          alias += ", ";
        }
      }
      td4.innerHTML = alias;
      td5.innerHTML = command["cooldown"] == undefined ? 0 : command["cooldown"] + "s";
      tr.appendChild(td1);
      tr.appendChild(td2);
      tr.appendChild(td3);
      tr.appendChild(td4);
      tr.appendChild(td5);
      table.appendChild(tr);
      commandCard.appendChild(content);
      commandsContainer.appendChild(commandCard)
    });
  }
  setListenerCollapsible();
}

function setListenerCollapsible() {
  var coll = document.getElementsByClassName("collapsible");
  for (i = 0; i < coll.length; i++) {
    coll[i].addEventListener("click", function () {
      this.classList.toggle("active");
      var content = this.nextElementSibling;
      if (content.style.display === "block") {
        content.style.display = "none";
      } else {
        content.style.display = "block";
      }
    });
  }
}


