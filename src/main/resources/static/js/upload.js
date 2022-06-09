/// <summary>
/// 格式化文件大小的JS方法
/// </summary>
/// <param name="filesize">文件的大小,传入的是一个bytes为单位的参数</param>
/// <returns>格式化后的值</returns>
function renderSize(value){
    if(null==value||value===''||value==='0'){
        return "0 B";
    }
    var unitArr = ["B","KB","MB","GB","TB","PB","EB","ZB","YB"];
    var index;
    var srcsize = parseFloat(value);
    index=Math.floor(Math.log(srcsize)/Math.log(1024));
    var size =srcsize/Math.pow(1024,index);
    size=size.toFixed(2);//保留的小数位数
    return size+unitArr[index];
}

