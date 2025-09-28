const path = require('path');

module.exports = function override(config, env) {
  // Add an alias for "@"
  config.resolve.alias['@'] = path.resolve(__dirname, 'src');

  // Add other custom configuration here

  return config;
};
