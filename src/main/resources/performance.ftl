<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>ASM - Performance</title>
  <link rel="stylesheet" type="text/css" href="style.css">
  <style>
    .chart {
      width: 80%;
      margin: 0 auto;
    }

    .chart p {
      margin: 0;
    }

    .chart .caption {
      float: right;
    }

    .chart ul {
      padding: 0;
      margin: 0;
      background-color: rgb(250, 250, 250);
      list-style-type: none;
    }

    .chart li {
      position: relative;
      width: 100%;
      height: 1.5em;
      margin: 4px 0;
    }

    .chart li span {
      display: flex;
      position: absolute;
      left: 0;
      top: 0;
      right: 0;
      bottom: 0;
      padding: 0 2px;
      align-items: center;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .chart li span:nth-child(1) {
      padding: 0;
      background-color: rgb(0, 158, 255);
    }

    .chart li span:nth-child(2) {
      top: 25%;
      bottom: 25%;
      padding: 0;
      border-left: 1px solid rgb(51, 51, 51);
      border-right: 1px solid rgb(51, 51, 51);
    }

    .chart li span:nth-child(2)::before {
      position: absolute;
      left: 0;
      top: 50%;
      right: 0;
      bottom: 50%;
      border-top: 1px solid rgb(51, 51, 51);
      content: '';
    }
  </style>
</head>
<body>
  <!--header-->
  <div class="page-content">
    <h1>ASM Performance Benchmarks</h1>
    <p>Each chart shows either the number of "operations" per second that can be
    performed by several versions of ASM and other libraries (the larger the
    better), or the number of memory bytes used by one operation (the smaller
    the better). The definition of an "operation" depends on the benchmark, and
    is given below for each benchmark. The "few dozen classes" mentioned below
    are the same for all benchmarks.</p>
    <p>This page is generated automatically from the results of the
    <a href="http://openjdk.java.net/projects/code-tools/jmh/">JMH</a> ASM
    benchmarks.</p>
    <#list benchmarks as benchmark>
      <h2 id="${benchmark.name}">${benchmark.name}</h2>
      <p>
      <#switch benchmark.name>
        <#case "AdapterBenchmark.getClassInfo">
          One operation = create a ClassReader and call getAccess(),
          getClassName(), getSuperName(), and getInterfaces() on it, for a few 
          dozen classes.
          <#break>
        <#case "AdapterBenchmark.getClassObjectModel">
          One operation = create a ClassReader and a ClassNode from it, for a
          few dozen classes.
          <#break>
        <#case "AdapterBenchmark.readAndWriteWithComputeFrames">
          One operation = create a ClassReader and make a ClassWriter with the
          COMPUTE_FRAMES option visit and serialize it, for a few dozen
          classes.
          <#break>
        <#case "AdapterBenchmark.readAndWriteWithComputeMaxs">
          One operation = create a ClassReader and make a ClassWriter with the
          COMPUTE_MAXS option visit and serialize it (or the equivalent for the
          other libraries), for a few dozen classes.
          <#break>
        <#case "AdapterBenchmark.readAndWriteWithCopyPool">
          One operation = create a ClassReader and make a ClassWriter with the
          "copy constant pool" optimization visit and serialize it, for a few
          dozen classes.
          <#break>
        <#case "AdapterBenchmark.readAndWriteWithObjectModel">
          One operation = create a ClassReader and make a ClassWriter visit and
          serialize it, via a ClassNode, for a few dozen classes.
          <#break>
        <#case "AdapterBenchmark.readAndWrite">
          One operation = create a ClassReader and make a ClassWriter visit and
          serialize it (or the equivalent for the other libraries), for a few
          dozen classes.
          <#break>
        <#case "AdapterBenchmark.read">
          One operation = create a ClassReader and make an empty visitor visit
          it, for a few dozen classes.
          <#break>
        <#case "GeneratorBenchmark">
          One operation = generate a HelloWorld class in byte array form.
          <#break>
        <#case "MemoryBenchmark.newClassNode">
          One operation = create, populate and store a ClassNode for a few dozen
          classes.
          <#break>
        <#case "MemoryBenchmark.newClass">
          One operation = create, populate and store a ClassWriter for a few
          dozen classes.
          <#break>
        <#default>
      </#switch>
      </p>
      <div class="chart">
        <p>0 <span class="caption">${benchmark.caption}</span></p>       
        <ul>
          <#list benchmark.elements as element>
            <li>
              <span style="right:${100 - element.value}%;">
                &nbsp;${element.name}
              </span>
              <span style="left:${element.minValue}%;
                           right:${100 - element.maxValue}%;">
              </span>
            </li>
          </#list>
        </ul>
      </div>
    </#list>
  </div>
</body>
</html>
