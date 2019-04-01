# Scatter
<h3>模仿EventBus的事件发送功能</h3>
<h4>用法</h4>
<b>注:ScatterUtil和Scatter的区别是使用ScatterUtil不用每次都调用getInstance()</b><br/><br/>
<ol>
  <li>在对象创建的时候使用register注册当前对象</li>
  <li>将要接收的方法贴上Recive标签</li>
    <ul type="disc">
       <li>threadMode:该方法要在什么线程执行,默认调用post时的线程,其他参数源码有解释</li>
       <li>priority:方法的优先级,默认0</li>
       <li>tag:标记,如果发送的时候不想发送到所有的方法,就可以使用tag,然后发送的时候就会找到tag符合发送时的tag的方法</li>
    </ul>
  <li>几个post方法</li>
    <ul type="disc">
       <li>post:向符合方法签名的方法发送事件</li>
       <li>postContainTag:向符合方法签名并且tag不为空的方法发送事件</li>
       <li>postTag:向Tag相同的、方法签名相同的方法发送事件</li>
    </ul>
  <li>在对象要销毁的时候使用unregister注销当前对象</li>
</ol>
