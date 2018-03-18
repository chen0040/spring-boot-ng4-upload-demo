

$currentPath = $pwd

if($args.length -gt 0) {
    $profile=$args[0]
}

Invoke-Expression -Command:"mvn -f pom.xml clean package"

$projs=@("spring-boot-application")
foreach ($proj in $projs){
    $source=$PSScriptRoot + "/target/" + $proj + ".jar"
    $dest=$PSScriptRoot + "/bin/" + $proj + ".jar"
    copy $source $dest
}

cd $currentPath
