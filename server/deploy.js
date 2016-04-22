var fs = require("fs-extra");
const exec = require('child_process').exec;
const execSync = require('child_process').execSync;

var deploy_dir = "C:/Users/tarab/Documents/reem/app/";
var inner_deploy = "app/";

var prev_repo = null;
var apply_stash = false;
var out = function () {
    if(apply_stash)
    {
        console.log("\n Reapplying stash...");
        try{
            shellSync("git checkout " + prev_repo, null, false, true);
            shellSync("git stash apply", null, false, true);
        }
        catch(e)
        {
            console.log("Couldn't apply stash.");
        }
    }
    process.exit(0);
};

var shell = function(command, cwd, cb){
    var options = cwd ? {cwd: cwd, stdio:"inherit"}:{stdio:"inherit"};
    exec(command,options,
        (error, stdout, stderr) => {
            cb(error);
        });
};

var shellSync = function(command, cwd, dontShow, throwExc)
{
    var options = cwd ? {cwd: cwd}:{};
    options.stdio = !dontShow ? 'inherit':'pipe';
    var result = null;
    if(!throwExc)
    {
        try{
            result = execSync(command,options);
        }
        catch (e)
        {
            console.log("Error.");
            out();
        }
    }
    else
    {
        result = execSync(command,options);
    }
    return result && result.toString().trim();

};

var mulShell = function (commands, cwd, cb) {
    if(typeof(cwd) == 'function')
    {
        cb = cwd;
        cwd = null;
    }
    shell(commands.shift(), cwd, function (error) {
        if(error)
        {
            cb(error);
        }
        else
        {
            if(commands.length) mulShell(commands, cwd, cb);
            else cb(null);
        }
    })
};


//get current branch name
var branch = shellSync("git rev-parse --abbrev-ref HEAD", null, true);
//if not in master try to stash before going there
if(branch != "master")
{
    prev_repo = branch;
    var stash_result = shellSync("git stash", null, true);
    if(stash_result != "No local changes to save")
    {
        apply_stash = true;
        console.log("Stashing..");
    }

    //ensure in master branch
    shellSync("git checkout master") ;
    var branch = shellSync("git rev-parse --abbrev-ref HEAD", null, true);
    if(branch.toString().trim() != "master")
    {
        console.log("Couldn't go to master branch, current branch is " + branch);
        out();
        return;
    }
}

//we are on master branch, pull it
shellSync("git pull");

//our master is up to date
console.log("Deploying....");
try{
    fs.copySync("./server/package.json",deploy_dir+"package.json",{clobber :true});
}
catch(error)
{
    console.log("Couldn't deploy package.json:  " + error);
    out();
};
console.log("Deployed package.json");

try {
    fs.emptyDirSync(deploy_dir+inner_deploy);
}
catch(e)
{
    console.log("Error Emptying app directory: " + e);
    out();
}
console.log("Emptied apps folder on server");

try {
    fs.copySync("./server/app/", deploy_dir + inner_deploy);
}
catch(error) {
    console.log("Couldn't deploy apps folder:  " + error);
    out();
}

console.log("Deployed app folder.");

console.log("Pushing to server");
// shellSync("git add -A", deploy_dir);
// shellSync("git commit -m 'New Deploy'", deploy_dir);
// shellSync("git push", deploy_dir);
// console.log("Deployed successfuly.");








