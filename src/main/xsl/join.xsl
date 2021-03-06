<?xml version="1.0"?>
<!--
Copyright (c) 2016-2018 Zerocracy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to read
the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:output method="html" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:include href="/xsl/inner-layout.xsl"/>
  <xsl:template match="page" mode="head">
    <title>
      <xsl:text>Join us</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="inner">
    <h1>
      <xsl:text>Join us</xsl:text>
    </h1>
    <p>
      <xsl:text>Fill out this form to send a join request to all high-ranked users.</xsl:text>
      <xsl:text> Someone may decide to </xsl:text>
      <a href="http://www.zerocracy.com/policy.html#1">
        <xsl:text>invite</xsl:text>
      </a>
      <xsl:text> you and become your mentor.</xsl:text>
      <xsl:text> Remember, that a mentor will receive financial bonuses from us</xsl:text>
      <xsl:text> and positive or negative </xsl:text>
      <a href="http://www.zerocracy.com/policy.html#18">
        <xsl:text>points</xsl:text>
      </a>
      <xsl:text> depending on your actions,</xsl:text>
      <xsl:text> so keep in mind that users will more likely invite you if you match</xsl:text>
      <xsl:text> the criteria listed in </xsl:text>
      <a href="http://www.yegor256.com/2014/10/29/how-much-do-you-cost.html">
        <xsl:text>this blog post</xsl:text>
      </a>
      <xsl:text>.</xsl:text>
    </p>
    <form method="post" action="/join-post">
      <fieldset>
        <label for="inp-personality">
          <xsl:text>Your </xsl:text>
          <a href="https://www.16personalities.com/free-personality-test">
            <xsl:text>personality type</xsl:text>
          </a>
          <xsl:text>:</xsl:text>
        </label>
        <input id="inp-personality" type="text" required="required" name="personality" placeholder="INTJ-A" size="8" maxlength="6" pattern="[IE][NS][TF][PJ]-[AT]"/>
        <label for="inp-about">
          <xsl:text>A few good words about you (keep it short):</xsl:text>
        </label>
        <textarea id="inp-about" required="required" name="about" style="width:100%;height:6em;"/>
        <label for="inp-stackoverflow">
          <xsl:text>Your </xsl:text>
          <a href="https://stackoverflow.com">
            <xsl:text>StackOverflow</xsl:text>
          </a>
          <xsl:text> user ID:</xsl:text>
        </label>
        <input id="inp-stackoverflow" type="number" placeholder="187141" size="6" name="stackoverflow"/>
        <label for="inp-telegram">
          <xsl:text>Your </xsl:text>
          <a href="https://telegram.org/">
            <xsl:text>Telegram</xsl:text>
          </a>
          <xsl:text> ID:</xsl:text>
        </label>
        <input id="inp-telegram" type="text" placeholder="username" name="telegram" size="10" required="required"/>
        <input type="submit"/>
      </fieldset>
    </form>
    <p>
      <xsl:text>Right after you submit this form,</xsl:text>
      <xsl:text> we will send your information to all users with 1024+ reputation.</xsl:text>
      <xsl:text> Some of them may become interested and will contact you in Telegram,</xsl:text>
      <xsl:text> but there is no guarantee.</xsl:text>
      <xsl:text> If you get no response in the next few days,</xsl:text>
      <xsl:text> feel free to fill out this form again.</xsl:text>
    </p>
  </xsl:template>
</xsl:stylesheet>
